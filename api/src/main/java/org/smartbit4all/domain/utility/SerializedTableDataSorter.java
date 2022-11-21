package org.smartbit4all.domain.utility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.TableDatas.SortProperty;
import org.smartbit4all.domain.utility.serialize.TableDataPager;

public class SerializedTableDataSorter {

  private static final int defaultChunkSize = 100;

  /**
   * Returns the sorted index list of the given serialized {@link TableData}
   */
  public static List<Integer> getSortedIndexes(TableDataPager<?> pager,
      List<SortProperty> sortProperties)
      throws Exception {
    return getSortedIndexes(pager, sortProperties, defaultChunkSize);
  }

  /**
   * Returns the sorted index list of the given serialized {@link TableData}
   * 
   * @param chunkSize number of rows to fetch for one page from the serialized data
   */
  public static List<Integer> getSortedIndexes(TableDataPager<?> pager,
      List<SortProperty> sortProperties, final int chunkSize) throws Exception {

    final int totalRowCount = pager.getTotalRowCount();

    // sort the table data in rounds, store the indexes
    Map<Integer, List<Integer>> sortedChunkIndexesByRound = new HashMap<>();
    int offset = 0;
    int roundCntr = 0;
    while (offset < totalRowCount) {
      TableData<?> tableDataPage = pager.fetch(offset, chunkSize);
      List<DataRow> originalPageRows = new ArrayList<>(tableDataPage.rows());
      TableDatas.sort(tableDataPage, sortProperties);

      List<Integer> sortedRoundIndexes = new ArrayList<>();
      for (DataRow dataRow : tableDataPage.rows()) {
        int orignalIndex = originalPageRows.indexOf(dataRow);
        sortedRoundIndexes.add(orignalIndex + roundCntr * chunkSize);
      }

      sortedChunkIndexesByRound.put(roundCntr, sortedRoundIndexes);
      offset += chunkSize;
      roundCntr++;
    }


    // init the cache
    Map<Integer, DataRow> cache = new LinkedHashMap<>(roundCntr);
    for (int i = 0; i < roundCntr; i++) {
      List<Integer> roundIndexes = sortedChunkIndexesByRound.get(i);
      Integer roundIdx = roundIndexes.remove(0);
      cache.put(roundIdx, seekRow(pager, roundIdx));
    }

    List<Integer> sortedIndexes = new ArrayList<>();
    Comparator<DataRow> dataRowComparator = TableDatas.getDataRowComparator(sortProperties);

    /*
     * fill the result index list: sorting the cache, put the first one to the result list, then
     * take the next index from the round that the current index was taken from
     */
    while (sortedIndexes.size() < totalRowCount) {
      // sort the cache
      cache = cache.entrySet()
          .stream()
          .sorted((entry1, entry2) -> {
            return dataRowComparator.compare(entry1.getValue(), entry2.getValue());
          })
          .collect(
              Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y,
                  LinkedHashMap::new));

      // get the first one from the cache
      Iterator<Integer> cacheIter = cache.keySet().iterator();
      Integer idx = cacheIter.next();
      cacheIter.remove();
      sortedIndexes.add(idx);

      // find the next index from the rounds and add to cache
      int sourceRound = idx / chunkSize;
      List<Integer> roundIndexes = sortedChunkIndexesByRound.get(sourceRound);
      if (!roundIndexes.isEmpty()) {
        Integer nextRoundIdx = roundIndexes.remove(0);
        cache.put(nextRoundIdx, seekRow(pager, nextRoundIdx));
      }
    }

    return sortedIndexes;
  }

  private static DataRow seekRow(TableDataPager<?> pager, int idx) throws Exception {
    TableData<?> tableData = pager.fetch(idx, 1);
    if (tableData.isEmpty()) {
      return null;
    }
    return TableDatas.copy(tableData).rows().get(0);
  }

}
