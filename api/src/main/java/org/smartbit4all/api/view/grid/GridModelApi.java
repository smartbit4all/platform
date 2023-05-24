package org.smartbit4all.api.view.grid;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.grid.bean.GridColumnMeta;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridUpdateData;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

public interface GridModelApi {

  /**
   * Creates a GridModel with a default GridView for use with clazz objects.
   *
   * @param clazz Class of object which will be used the grid with.
   * @param columns Which properties will be used from clazz in GridModel. If null or empty, all
   *        properties of the clazz will be used.
   * @param columnPrefix This will be used to get value from {@link LocaleSettingApi} and set it
   *        {@link GridColumnMeta#label(String)} as a header label/caption. If empty, clazz
   *        classname will be used.
   * @return
   */
  GridView createGridView(Class<?> clazz, List<String> columns, String... columnPrefix);

  /**
   * Creates a GridModel with a default GridView for use with clazz objects.
   *
   * @param entityDefinition EntityDefinition of tableData which will be used the grid with.
   * @param columns Which properties will be used from clazz in GridModel. If null or empty, all
   *        properties of the clazz will be used.
   * @param columnPrefix This will be used to get value from {@link LocaleSettingApi} and set it
   *        {@link GridColumnMeta#label(String)} as a header label/caption. If empty,
   *        {@link EntityDefinition#allProperties()} will be used.
   * @return
   */
  GridView createGridView(EntityDefinition entityDefinition, List<String> columns,
      String... columnPrefix);

  /**
   * Creates a GridModel with a default GridView for use with clazz objects. For parameters see
   * {@link #createGridView(Class, List, String...)}.
   *
   */
  GridModel createGridModel(Class<?> clazz, List<String> columns, String... columnPrefix);

  /**
   * Creates a GridModel with a default GridView for use with clazz objects. For parameters see
   * {@link #createGridView(EntityDefinition, List, String...)}.
   *
   * @param entityDefinition EntityDefinition of tableData which will be used the grid with.
   * @param columns Which properties will be used from clazz in GridModel. If null or empty, all
   *        properties of the clazz will be used.
   * @param columnPrefix This will be used to get value from {@link LocaleSettingApi} and set it
   *        {@link GridColumnMeta#label(String)} as a header label/caption. If empty,
   *        {@link EntityDefinition#allProperties()} will be used.
   * @return
   */
  GridModel createGridModel(EntityDefinition entityDefinition, List<String> columns,
      String... columnPrefix);

  /**
   * Initializes given GridModel in View. It can be used from {@link PageApi#initModel(View)}, but
   * in this case gridId cannot be model based, because model is not set yet. In this case we will
   * receive an IllegalArgumentException.
   *
   * @param viewUuid
   * @param gridId
   * @param gridModel
   */
  void initGridInView(UUID viewUuid, String gridId, GridModel gridModel);

  <T> void setData(UUID viewUuid, String gridId, Class<T> clazz, List<T> data);

  void setData(UUID viewUuid, String gridId, TableData<?> data);

  void setDataFromUris(UUID viewUuid, String gridId, SearchIndex<?> searchIndex, Stream<URI> uris);

  <T> void setDataFromObjects(UUID viewUuid, String gridId, SearchIndex<T> searchIndex,
      Stream<T> objects);

  @Deprecated
  <T> GridModel modelOf(Class<T> clazz, List<T> o, Map<String, String> columns,
      String... columnPrefix);

  /**
   * {@link Deprecated} Use createGridModel/View, initGridInView and setData methods instead.
   *
   */
  @Deprecated
  <T> GridModel modelOf(Class<T> clazz, List<T> o, Map<String, String> columns, int lowerBound,
      int upperBound, String... columnPrefix);

  /**
   * {@link Deprecated} Use createGridModel/View, initGridInView and setData methods instead.
   *
   * If we already have a table data as the result of a query and we would like to save it as
   * content of a grid.
   *
   * @param tableData The table data.
   * @return
   */
  @Deprecated
  GridModel modelOf(TableData<?> tableData, String... columnPrefix);

  /**
   * {@link Deprecated} Use createGridModel/View, initGridInView and setData methods instead.
   *
   * If we already have a table data as the result of a query and we would like to save it as
   * content of a grid.
   *
   * @param tableData The table data.
   * @param lowerBound lower bound of the initial page begin with 0 inclusively.
   * @param upperBound upper bound of the initial page exclusively.
   * @return
   */
  @Deprecated
  GridModel modelOf(TableData<?> tableData, int lowerBound, int upperBound, String... columnPrefix);

  /**
   * {@link Deprecated} Use createGridModel/View, initGridInView and setData methods instead.
   *
   * If we have a well defined list bean and we would like to save it's grid model by the definition
   * of a {@link SearchIndex}.
   *
   * @param searchIndex The search index to use to construct the table data result.
   * @param uris The list of URI.
   * @return
   */
  @Deprecated
  GridModel modelOfUris(SearchIndex<?> searchIndex, Stream<URI> uris, String... columnPrefix);

  /**
   * {@link Deprecated} Use createGridModel/View, initGridInView and setData methods instead.
   *
   * If we have a well defined list bean and we would like to save it's grid model by the definition
   * of a {@link SearchIndex}.
   *
   * @param <T>
   * @param searchIndex The search index to use to construct the table data result.
   * @param objects The list of objects.
   * @return
   */
  @Deprecated
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
