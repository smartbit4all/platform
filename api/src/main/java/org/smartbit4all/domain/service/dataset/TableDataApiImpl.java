package org.smartbit4all.domain.service.dataset;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy.OrderEnum;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.TableDatas.BuilderWithFixProperties;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.service.CrudApi;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.utility.serialize.TableDataPager;
import org.smartbit4all.domain.utility.serialize.TableDataSerializer;
import org.smartbit4all.storage.fs.StorageFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

/**
 *
 * The table data is always temporary data set that is used to save a database table like set of
 * data that can be used in SQL expressions based on the features of the {@link CrudApi}. The table
 * data api is based on file system {@link RandomAccess} to optimize the write and read of the huge
 * tables.
 *
 * @author Peter Boros
 *
 */
public class TableDataApiImpl implements TableDataApi {

  private static final String TABLEDATACONTENTS = "tabledatacontents";

  private static final String TABLEDATAFILEEXTESION = ".td";

  private static final int defaultChunkSize = 100;

  private static final Logger log = LoggerFactory.getLogger(TableDataApiImpl.class);

  private Map<URI, TableData<?>> tableDatas = new HashMap<>();

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private ObjectApi objectApi;

  /**
   * The root folder of the file system storage. If null then the storage of the table data files
   * will be located in the temporary directory of the jvm.
   */
  private File rootFolder = null;

  public TableDataApiImpl(@Autowired(required = false) StorageApi storageApi) {
    super();
    if (storageApi != null && storageApi.getDefaultObjectStorage() instanceof StorageFS) {
      rootFolder = new File(((StorageFS) storageApi.getDefaultObjectStorage()).getRootFolder(),
          TABLEDATACONTENTS);
    } else {
      try {
        File parentFile = File.createTempFile("tabledata", "root").getParentFile();
        rootFolder =
            new File(parentFile, TABLEDATACONTENTS);
        Files.delete(parentFile.toPath());
      } catch (IOException e) {
        log.error("Unable to initiate the temp directory to save table data serialized contents.");
      }
    }
    if (rootFolder != null && rootFolder.exists()) {
      rootFolder.mkdirs();
    }
  }

  @Override
  public URI save(TableData<?> tableData) {
    if (tableData == null) {
      return null;
    }
    URI uri = constructUri(UUID.randomUUID());
    if (rootFolder != null) {
      File fileByUri = FileIO.getFileByUri(rootFolder, uri, TABLEDATAFILEEXTESION);
      fileByUri.getParentFile().mkdirs();
      try (FileOutputStream os = new FileOutputStream(fileByUri)) {
        TableDataSerializer.save(tableData, os, objectApi);
      } catch (IOException e) {
        throw new IllegalStateException("Unable to construct the file for the table data.", e);
      }
    } else {
      tableDatas.put(uri, tableData);
    }
    tableData.setUri(uri);
    return uri;
  }

  @Override
  public TableData<?> read(URI uri) {
    TableData<?> result;
    if (rootFolder != null) {
      try {
        TableDataPager<?> pager = TableDataPager
            .create(FileIO.getFileByUri(rootFolder, uri, TABLEDATAFILEEXTESION), entityManager,
                objectApi);
        result = pager.fetchAll();
      } catch (Exception e) {
        throw new IllegalArgumentException("Unable to read the " + uri + " table data.", e);
      }
    } else {
      result = tableDatas.get(uri);
    }
    if (result != null) {
      result.setUri(uri);
    }
    return result;
  }

  @Override
  public TableData<?> readPage(URI uri, int offset, int limit) {
    if (rootFolder != null) {
      try {
        TableDataPager<?> pager = TableDataPager
            .create(FileIO.getFileByUri(rootFolder, uri, TABLEDATAFILEEXTESION), entityManager,
                objectApi);
        return pager.fetch(offset, limit);
      } catch (Exception e) {
        throw new IllegalArgumentException("Unable to read the " + uri + " table data.", e);
      }
    } else {
      return TableDatas.copyRows(tableDatas.get(uri), offset, offset + limit);
    }
  }

  @Override
  public void delete(URI uri) {
    tableDatas.remove(uri);
  }

  private final URI constructUri(UUID uuid) {
    LocalDateTime now = LocalDateTime.now();
    return URI.create(TABLEDATACONTENTS + StringConstant.COLON + StringConstant.SLASH
        + now.getYear() + StringConstant.SLASH + now.getMonthValue() + StringConstant.SLASH
        + now.getDayOfMonth() + StringConstant.SLASH + now.getHour() + StringConstant.SLASH
        + now.getMinute() + StringConstant.SLASH
        + now.getSecond() + StringConstant.SLASH
        + uuid);
  }

  @Override
  public <T> TableData<?> tableOf(Class<T> clazz, List<T> objectList, List<String> columns) {
    EntityDefinition entityDefinition = entityManager.createEntityDef(clazz);
    entityManager.registerEntityDef(entityDefinition);
    return tableOf(entityDefinition, objectList, columns);
  }

  @Override
  public <T> TableData<?> tableOf(EntityDefinition entityDef, List<T> objectList,
      List<String> columns) {
    // in case of embedded column names (which contains a DOT) we only need the main property (first
    // element of property
    // path)
    List<String> tableColumns = columns.stream()
        .map(col -> col.split(StringConstant.DOT_REGEX)[0])
        .distinct()
        .collect(toList());
    Map<String, Property<?>> properties = tableColumns.stream()
        .collect(toMap(col -> col, entityDef::getProperty));
    BuilderWithFixProperties<EntityDefinition> table =
        TableDatas.builder(entityDef, properties.values());
    objectList.stream()
        .forEachOrdered(object -> {
          BuilderWithFixProperties<EntityDefinition> row = table.addRow();
          Map<String, Object> map = objectApi.create(null, object).getObjectAsMap();
          tableColumns.forEach(
              col -> row.setObject(properties.get(col), map.get(col)));
        });
    return table.build(objectApi);
  }



  @Override
  public <E extends EntityDefinition> void sort(TableData<E> tableData,
      List<SortOrderProperty> sortProperties) {
    sort(tableData, sortProperties, false);
  }

  private <E extends EntityDefinition> Property<?> getComplexColumn(TableData<E> tableData,
      String propertyName) {
    if (propertyName.contains(StringConstant.DOT)) {
      String[] path = propertyName.split(StringConstant.DOT_REGEX);
      String column = path[0];
      return tableData.entity().getProperty(column);
    }
    return null;
  }

  @Override
  public <E extends EntityDefinition> void sortByFilterExpression(TableData<E> tableData,
      List<FilterExpressionOrderBy> sortProperties) {
    List<SortOrderProperty> sortOrders = sortProperties.stream()
        .map(orderBy -> orderBy.getOrder() == OrderEnum.DESC
            ? tableData.entity().getProperty(orderBy.getPropertyName()).desc()
            : tableData.entity().getProperty(orderBy.getPropertyName()).asc())
        .collect(toList());
    sort(tableData, sortOrders);
  }

  @Override
  public <E extends EntityDefinition> TableData<E> sort(TableData<E> tableData,
      List<SortOrderProperty> sortProperties, boolean toNewTableData) {


    Objects.requireNonNull(tableData, "tableData can not be null!");
    if (ObjectUtils.isEmpty(sortProperties)) {
      throw new IllegalArgumentException("sortProperties can not be null nor empty!");
    }

    // check the sortProperties
    for (SortOrderProperty sortProp : sortProperties) {
      if (tableData.getColumn(sortProp.property) == null) {
        Property<?> complexColumn = getComplexColumn(tableData, sortProp.property.getName());
        if (complexColumn == null || tableData.getColumn(complexColumn) == null)
          throw new IllegalArgumentException(
              "The given TableData has no property with the descibed SortOrderProperty: ["
                  + sortProp.property.getUri() + "]!");

      }
    }

    if (toNewTableData) {
      tableData = TableDatas.copy(tableData);
    }
    sortRows(tableData.rows(), sortProperties);

    return tableData;
  }

  private void sortRows(List<DataRow> rows, List<SortOrderProperty> sortProperties) {
    Collections.sort(rows, getDataRowComparator(sortProperties));
  }

  private Comparator<DataRow> getDataRowComparator(List<SortOrderProperty> sortProperties) {
    return (row1, row2) -> {
      for (SortOrderProperty sortProp : sortProperties) {
        Property<?> prop = sortProp.property;
        Comparator<Object> comparator = (Comparator<Object>) prop.getComparator();
        if (comparator == null) {
          return 0;
        }
        Object o1 = getObject(row1, prop);
        Object o2 = getObject(row2, prop);
        int res = comparator.compare(o1, o2);
        if (res != 0) {
          if (o1 == null) {
            return sortProp.nullsFirst ? -1 : 1;
          } else if (o2 == null) {
            return sortProp.nullsFirst ? 1 : -1;
          }
          // invert only when neither o1 nor o2 is null, nullsFirst is stronger than asc (??)
          return sortProp.asc ? res : res * -1;
        }
      }

      return 0;
    };
  }

  private Object getObject(DataRow row, Property<?> prop) {
    if (row.tableData().getColumn(prop) == null) {
      Property<?> complexColumn = getComplexColumn(row.tableData(), prop.getName());
      if (complexColumn != null) {
        Object object = row.get(complexColumn);
        String[] path = prop.getName().split(StringConstant.DOT_REGEX);
        String[] restPath = Arrays.copyOfRange(path, 1, path.length);
        return objectApi.getValueFromObject(prop.type(), object, restPath);
      }
    }

    return row.get(prop);
  }

  @Override
  public List<Integer> getSortedIndexes(TableDataPager<?> pager,
      List<SortOrderProperty> sortProperties)
      throws Exception {
    return getSortedIndexes(pager, sortProperties, defaultChunkSize);
  }

  /**
   * Returns the sorted index list of the given serialized {@link TableData}
   *
   * @param chunkSize number of rows to fetch for one page from the serialized data
   */
  @Override
  public List<Integer> getSortedIndexes(TableDataPager<?> pager,
      List<SortOrderProperty> sortProperties, final int chunkSize) throws Exception {

    final int totalRowCount = pager.getTotalRowCount();

    // sort the table data in rounds, store the indexes
    Map<Integer, List<Integer>> sortedChunkIndexesByRound = new HashMap<>();
    int offset = 0;
    int roundCntr = 0;
    while (offset < totalRowCount) {
      TableData<?> tableDataPage = pager.fetch(offset, chunkSize);
      List<DataRow> originalPageRows = new ArrayList<>(tableDataPage.rows());
      sort(tableDataPage, sortProperties);

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
    Comparator<DataRow> dataRowComparator = getDataRowComparator(sortProperties);

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

  private DataRow seekRow(TableDataPager<?> pager, int idx) throws Exception {
    TableData<?> tableData = pager.fetch(idx, 1);
    if (tableData.isEmpty()) {
      return null;
    }
    return TableDatas.copy(tableData).rows().get(0);
  }


}
