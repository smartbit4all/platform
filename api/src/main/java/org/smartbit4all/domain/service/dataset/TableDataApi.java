package org.smartbit4all.domain.service.dataset;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.service.CrudApi;
import org.smartbit4all.domain.utility.serialize.TableDataPager;

/**
 * The {@link TableDataApi} is responsible for storing and retrieving the {@link TableData}s as a
 * set of record. The {@link CrudApi} use this to manage the temporary record sets during the query
 * process and there can be record sets with longer storage period.
 *
 * @author Peter Boros
 */
public interface TableDataApi {

  /**
   * This saves a table data.
   *
   * @param tableData The table data itself.
   * @return The URI for reading.
   */
  URI save(TableData<?> tableData);

  /**
   * Read the {@link TableData} identified by the URI.
   *
   * @param uri The unique identifier.
   * @return
   */
  TableData<?> read(URI uri);

  /**
   * Read the {@link TableData} identified by the URI.
   *
   * @param uri The unique identifier.
   * @param offset The row index to fetch the data from.
   * @param limit The number of rows to fetch.
   * @return The content of the page.
   */
  TableData<?> readPage(URI uri, int offset, int limit);

  void delete(URI uri);

  <T> TableData<?> tableOf(EntityDefinition entityDef, List<T> objectList, List<String> columns);

  <T> TableData<?> tableOf(Class<T> clazz, List<T> objectList, List<String> columns);


  /**
   * Sorts the rows of the given {@link TableData} by the given {@link SortOrderProperty}-ies.</br>
   *
   * @param <E> The {@link EntityDefinition} of the {@link TableData}
   * @param tableData The source {@link TableData}
   * @param sortProperties The properties describing the sort orders
   */
  <E extends EntityDefinition> void sort(TableData<E> tableData,
      List<SortOrderProperty> sortProperties);

  <E extends EntityDefinition> void sortByFilterExpression(TableData<E> tableData,
      List<FilterExpressionOrderBy> sortProperties);

  /**
   * Sorts the rows of the given {@link TableData} by the given {@link SortOrderProperty}-ies.</br>
   * The source and the result TableData both will exist in the memory, so be aware of the double
   * memory consumption.
   *
   * @param <E> The {@link EntityDefinition} of the {@link TableData}
   * @param tableData The source {@link TableData}
   * @param sortProperties The properties describing the sort orders
   * @param toNewTableData if true then the original {@link TableData} will not be touched and
   *        creates a new {@link TableData} that is sorted by the given
   *        {@link SortOrderProperty}-ies. The source and the result TableData both will exist in
   *        the memory, so be aware of the double memory consumption.
   * @return The new sorted {@link TableData}
   */
  <E extends EntityDefinition> TableData<E> sort(TableData<E> tableData,
      List<SortOrderProperty> sortProperties, boolean toNewTableData);

  /**
   * Returns the sorted index list of the given serialized {@link TableData}
   */
  List<Integer> getSortedIndexes(TableDataPager<?> pager, List<SortOrderProperty> sortProperties)
      throws Exception;

  /**
   * Returns the sorted index list of the given serialized {@link TableData}
   *
   * @param chunkSize number of rows to fetch for one page from the serialized data
   */
  List<Integer> getSortedIndexes(TableDataPager<?> pager, List<SortOrderProperty> sortProperties,
      int chunkSize) throws Exception;

}
