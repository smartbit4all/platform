package org.smartbit4all.api.view.grid;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridUpdateData;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.domain.data.TableData;

public interface GridModelApi {

  /**
   * If we have a well defined list bean and we would like to save it.
   *
   * @param <T>
   * @param clazz
   * @param o
   * @param columns
   * @return
   */
  // TODO columns should be List<>
  <T> GridModel modelOf(Class<T> clazz, List<T> o, Map<String, String> columns,
      String... columnPrefix);

  // TODO columns should be List<>
  <T> GridModel modelOf(Class<T> clazz, List<T> o, Map<String, String> columns, int lowerBound,
      int upperBound, String... columnPrefix);

  /**
   * If we already have a table data as the result of a query and we would like to save it as
   * content of a grid.
   *
   * @param tableData The table data.
   * @return
   */
  GridModel modelOf(TableData<?> tableData, String... columnPrefix);

  /**
   * If we already have a table data as the result of a query and we would like to save it as
   * content of a grid.
   *
   * @param tableData The table data.
   * @param lowerBound lower bound of the initial page begin with 0 inclusively.
   * @param upperBound upper bound of the initial page exclusively.
   * @return
   */
  GridModel modelOf(TableData<?> tableData, int lowerBound, int upperBound, String... columnPrefix);

  /**
   * If we have a well defined list bean and we would like to save it's grid model by the definition
   * of a {@link SearchIndex}.
   *
   * @param searchIndex The search index to use to construct the table data result.
   * @param uris The list of URI.
   * @return
   */
  GridModel modelOfUris(SearchIndex<?> searchIndex, Stream<URI> uris, String... columnPrefix);

  /**
   * If we have a well defined list bean and we would like to save it's grid model by the definition
   * of a {@link SearchIndex}.
   *
   * @param <T>
   * @param searchIndex The search index to use to construct the table data result.
   * @param objects The list of objects.
   * @return
   */
  <T> GridModel modelOfObjects(SearchIndex<T> searchIndex, Stream<T> objects,
      String... columnPrefix);

  /**
   * Do the paging on the model
   *
   * @param model The model object
   * @param offset The row index to fetch the data from.
   * @param limit The number of rows to fetch.
   * @return
   */
  GridModel loadPage(GridModel model, int offset, int limit);

  GridModel updateGrid(GridModel model, GridUpdateData update);

  void setColumnOrder(GridModel model, List<String> columns);

  /**
   * Sort and overwrite the same... The unsorted is the default and if we have sortOrderList then we
   * apply after the load.
   *
   * @param model
   * @param orderByList
   */
  void setOrderBy(GridModel model, List<FilterExpressionOrderBy> orderByList);

  <T> T executeGridCall(UUID viewUuid, String gridId, Function<GridModel, T> gridCall);

  Object expand(GridModel grid, String gridId, String rowId);

  void addExpandCallback(UUID viewUuid, String gridId, InvocationRequest request);

}
