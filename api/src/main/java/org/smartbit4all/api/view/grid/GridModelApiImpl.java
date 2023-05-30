package org.smartbit4all.api.view.grid;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.grid.bean.GridColumnMeta;
import org.smartbit4all.api.grid.bean.GridDataAccessConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridUpdateData;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.grid.bean.GridViewDescriptor;
import org.smartbit4all.api.grid.bean.GridViewDescriptor.KindEnum;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.base.Strings;

public class GridModelApiImpl implements GridModelApi {

  private static final Integer DEFAULT_PAGE_SIZE = 10;

  private static final List<Integer> DEFAULT_PAGE_SIZE_OPTIONS = Arrays.asList(5, 10, 25, 50);

  private static final Logger log = LoggerFactory.getLogger(GridModelApiImpl.class);

  private static final String EXPAND_POSTFIX = "_expandCallback";

  private static final String GRIDROW_POSTFIX = "_gridrowCallback";

  private static final String GRIDPAGE_POSTFIX = "_gridpageCallback";

  @Value("${grid.pageSizeOptions:5,10,25,50}")
  private List<String> defaultPageSizeOptions;

  @Value("${grid.pageSize:10}")
  private String defaultPageSize;

  @Autowired
  private ObjectApi objectApi;

  // FIXME (viewApi is not present everywhere)
  @Autowired(required = false)
  private ViewApi viewApi;

  @Autowired
  private TableDataApi tableDataApi;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Autowired
  private InvocationApi invocationApi;

  @Override
  public GridView createGridView(Class<?> clazz, List<String> columns, String... columnPrefix) {
    return createGridView(entityManager.createEntityDef(clazz), columns, columnPrefix);
  }

  @Override
  public GridView createGridView(EntityDefinition entityDefinition, List<String> columns,
      String... columnPrefix) {
    String[] keys;
    int idxLastKey;
    if (columnPrefix == null) {
      keys = new String[1];
      idxLastKey = 0;
    } else {
      keys = new String[columnPrefix.length + 1];
      System.arraycopy(
          columnPrefix, 0,
          keys, 0,
          columnPrefix.length);
      idxLastKey = columnPrefix.length;
    }
    if (columns == null || columns.isEmpty()) {
      throw new IllegalArgumentException("Empty columns is not supported yet!");
    }
    List<GridColumnMeta> headers = new ArrayList<>();
    for (String column : columns) {
      keys[idxLastKey] = column;
      headers.add(new GridColumnMeta().propertyName(column)
          .label(localeSettingApi.get(keys))
          .typeClass(entityDefinition.getProperty(column).type().getName()));
    }
    return new GridView()
        .descriptor(new GridViewDescriptor()
            .kind(KindEnum.TABLE)
            .columns(headers))
        .orderedColumnNames(columns);
  }

  @Override
  public GridModel createGridModel(Class<?> clazz, List<String> columns, String... columnPrefix) {
    return createEmptyGridModel(createGridView(clazz, columns, columnPrefix));
  }

  @Override
  public GridModel createGridModel(EntityDefinition entityDefinition, List<String> columns,
      String... columnPrefix) {
    return createEmptyGridModel(createGridView(entityDefinition, columns, columnPrefix));
  }

  private GridModel createEmptyGridModel(GridView defaultView) {
    return new GridModel()
        .totalRowCount(0)
        .view(defaultView)
        .page(new GridPage()
            .rows(new ArrayList<>())
            .lowerBound(0)
            .upperBound(0))
        .addAvailableViewsItem(defaultView);
  }

  @Override
  public void initGridInView(UUID viewUuid, String gridId, GridModel gridModel) {
    Objects.requireNonNull(gridModel, "GridModel cannot be null");
    Objects.requireNonNull(gridId, "GridId cannot be null");
    if (gridId.startsWith(View.MODEL + StringConstant.DOT)) {
      View view = viewApi.getView(viewUuid);
      if (view.getModel() == null) {
        throw new IllegalArgumentException(
            "Trying to init grid (" + gridId + ") in empty model, probably called from initModel!");
      }
    }
    if (gridModel.getPageSize() == null) {
      gridModel.setPageSize(getDefaultPageSize());
    }
    if (gridModel.getPageSizeOptions() == null || gridModel.getPageSizeOptions().isEmpty()) {
      gridModel.setPageSizeOptions(getDefaultPageSizeOptions());
    }
    gridModel.setViewUuid(viewUuid);
    viewApi.setWidgetModelInView(GridModel.class, viewUuid, gridId, gridModel);
  }

  private Integer getDefaultPageSize() {
    Integer pageSize;
    try {
      pageSize = Integer.valueOf(defaultPageSize);
    } catch (NumberFormatException e) {
      log.warn("Invalid pageSize setting: {}", defaultPageSize);
      pageSize = DEFAULT_PAGE_SIZE;
    }
    return pageSize;
  }

  private List<Integer> getDefaultPageSizeOptions() {
    List<Integer> pageSizeOptions;
    try {
      pageSizeOptions = defaultPageSizeOptions.stream()
          .map(Integer::valueOf)
          .collect(toList());
    } catch (NumberFormatException e) {
      log.warn("Invalid pageSizeOptions setting: {}", defaultPageSizeOptions);
      pageSizeOptions = DEFAULT_PAGE_SIZE_OPTIONS;
    }
    return pageSizeOptions;
  }

  @Override
  public <T> void setData(UUID viewUuid, String gridId, Class<T> clazz, List<T> data) {
    executeGridCall(viewUuid, gridId, gridModel -> {
      List<String> columns = gridModel.getView().getDescriptor().getColumns().stream()
          .map(GridColumnMeta::getPropertyName)
          .collect(toList());
      setData(viewUuid, gridId, tableDataApi.tableOf(clazz, data, columns));
      return 0;
    });
  }

  @Override
  public void setData(UUID viewUuid, String gridId, TableData<?> data) {
    executeGridCall(viewUuid, gridId, gridModel -> {
      if (data.getUri() == null) {
        tableDataApi.save(data);
      }
      gridModel.accessConfig(new GridDataAccessConfig().dataUri(data.getUri()));
      gridModel.totalRowCount(data.size());
      Integer pageSize = gridModel.getPageSize();
      if (pageSize == null) {
        pageSize = getDefaultPageSize();
        gridModel.setPageSize(pageSize);
      }
      int firstPageSize = Math.min(pageSize, data.size());
      gridModel
          .page(constructPage(viewUuid, gridId, data, 0, firstPageSize)
              .lowerBound(0)
              .upperBound(firstPageSize));
      return firstPageSize;
    });
  }

  @Override
  public void setDataFromUris(UUID viewUuid, String gridId, SearchIndex<?> searchIndex,
      Stream<URI> uris) {
    TableData<?> data = searchIndex.tableDataOfUris(uris);
    setData(viewUuid, gridId, data);
  }

  @Override
  public <T> void setDataFromObjects(UUID viewUuid, String gridId, SearchIndex<T> searchIndex,
      Stream<T> objects) {
    TableData<?> data = searchIndex.tableDataOfObjects(objects);
    setData(viewUuid, gridId, data);
  }

  @Override
  @Deprecated
  public <T> GridModel modelOf(Class<T> clazz, List<T> objectList, Map<String, String> columns,
      String... columnPrefix) {
    return modelOf(clazz, objectList, columns, 0, objectList.size(), columnPrefix);
  }

  @Override
  @Deprecated
  public <T> GridModel modelOf(Class<T> clazz, List<T> objectList,
      Map<String, String> columns, int lowerBound, int upperBound, String... columnPrefix) {
    return modelOf(
        tableDataApi.tableOf(clazz, objectList, new ArrayList<>(columns.keySet())),
        lowerBound,
        upperBound,
        columnPrefix);
  }

  @Override
  @Deprecated
  public GridModel modelOf(TableData<?> tableData, String... columnPrefix) {
    return constructModel(tableData, 0, tableData.size(), columnPrefix);
  }

  @Override
  @Deprecated
  public GridModel modelOf(TableData<?> tableData, int lowerBound, int upperBound,
      String... columnPrefix) {
    return constructModel(tableData, lowerBound, upperBound, columnPrefix);
  }

  private GridModel constructModel(TableData<?> tableData, int lowerBound, int upperBound,
      String... columnPrefix) {
    if (tableData.getUri() == null) {
      tableDataApi.save(tableData);
    }
    List<String> columns = tableData.columns().stream()
        .map(DataColumn::getName)
        .collect(toList());
    GridModel result = createGridModel(tableData.entity(), columns, columnPrefix);
    result.accessConfig(new GridDataAccessConfig().dataUri(tableData.getUri()));
    result.totalRowCount(tableData.size());
    result.page(
        constructPage(null, null, tableData, lowerBound, upperBound)
            .lowerBound(lowerBound)
            .upperBound(upperBound));
    return result;
  }

  @Override
  @Deprecated
  public GridModel modelOfUris(SearchIndex<?> searchIndex, Stream<URI> uris,
      String... columnPrefix) {
    TableData<?> tableData = searchIndex.tableDataOfUris(uris);
    return constructModel(tableData, 0, tableData.size(), columnPrefix);
  }

  @Override
  @Deprecated
  public <T> GridModel modelOfObjects(SearchIndex<T> searchIndex, Stream<T> objects,
      String... columnPrefix) {
    TableData<?> tableData = searchIndex.tableDataOfObjects(objects);
    return constructModel(tableData, 0, tableData.size(), columnPrefix);
  }

  @Override
  public GridModel loadPage(UUID viewUuid, String gridId, int offset, int limit) {
    return executeGridCall(viewUuid, gridId, model -> {
      if (model.getAccessConfig() == null) {
        // TODO update / setPageSize?
        return model;
      }
      TableData<?> tableData =
          tableDataApi.readPage(model.getAccessConfig().getDataUri(), offset, limit);
      model.page(constructPage(viewUuid, gridId, tableData, 0, tableData.size())
          .lowerBound(offset)
          .upperBound(offset + limit));
      return model;
    });
  }

  @Override
  public GridModel updateGrid(UUID viewUuid, String gridId, GridUpdateData update) {
    return executeGridCall(viewUuid, gridId, model -> {
      // check update to match existing columns
      List<@NotNull String> validColumns = model.getView().getDescriptor().getColumns().stream()
          .map(col -> col.getPropertyName())
          .collect(toList());
      if (update.getOrderedColumnNames().stream()
          .anyMatch(col -> !validColumns.contains(col))) {
        throw new IllegalArgumentException("Invalid ordered columnName in update");
      }
      // any check??
      model.getView().setOrderedColumnNames(update.getOrderedColumnNames());
      if (!update.getOrderByList().isEmpty()) {
        if (update.getOrderByList().stream()
            .map(col -> col.getPropertyName())
            .anyMatch(col -> !validColumns.contains(col))) {
          throw new IllegalArgumentException("Invalid orderByList columnName in update");
        }
        if (model.getAccessConfig() != null && model.getAccessConfig().getDataUri() != null) {
          TableData<?> data = tableDataApi.read(model.getAccessConfig().getDataUri());
          TableDatas.sortByFilterExpression(data, update.getOrderByList());
          tableDataApi.save(data);
          model.getAccessConfig().setDataUri(data.getUri());
          int lowerBound = model.getPage().getLowerBound();
          int upperBound = model.getPage().getUpperBound();
          model.page(constructPage(viewUuid, gridId, data, lowerBound,
              Math.min(data.size(), upperBound))
                  .lowerBound(lowerBound)
                  .upperBound(upperBound));
        }
      }
      model.getView().setOrderByList(update.getOrderByList());
      return model;
    });
  }

  private GridPage constructPage(UUID viewUuid, String gridId, TableData<?> tableData,
      int beginIndex, int endIndex) {
    GridPage page = new GridPage();
    page.rows(new ArrayList<>());
    InvocationRequest gridRowCallback = getCallback(viewUuid, gridId, GRIDROW_POSTFIX);
    for (int i = beginIndex; i < endIndex; i++) {
      DataRow dataRow = tableData.rows().get(i);
      GridRow gridRow = new GridRow().id(Integer.toString(i)).data(tableData.columns().stream()
          .filter(c -> tableData.get(c, dataRow) != null)
          .collect(toMap(DataColumn::getName, c -> tableData.get(c, dataRow))));
      gridRow = (GridRow) executeCallback(gridRowCallback, gridRow);
      page.addRowsItem(gridRow);
    }
    InvocationRequest gridPageCallback = getCallback(viewUuid, gridId, GRIDPAGE_POSTFIX);
    return (GridPage) executeCallback(gridPageCallback, page);
  }

  @Override
  public void setColumnOrder(GridModel model, List<String> columns) {
    model.getView().setOrderedColumnNames(columns);
  }

  @Override
  public void setOrderBy(GridModel model, List<FilterExpressionOrderBy> orderByList) {}

  @Override
  public <T> T executeGridCall(UUID viewUuid, String gridId, Function<GridModel, T> gridCall) {
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
    gridModel.setViewUuid(viewUuid);
    T result = gridCall.apply(gridModel);
    viewApi.setWidgetModelInView(GridModel.class, viewUuid, gridId, gridModel);
    return result;
  }

  @Override
  public void setExpandCallback(UUID viewUuid, String gridId, InvocationRequest request) {
    addCallback(viewUuid, gridId, request, EXPAND_POSTFIX);
  }

  @Override
  public void addGridRowCallback(UUID viewUuid, String gridId, InvocationRequest request) {
    addCallback(viewUuid, gridId, request, GRIDROW_POSTFIX);
  }

  @Override
  public void addGridPageCallback(UUID viewUuid, String gridId, InvocationRequest request) {
    addCallback(viewUuid, gridId, request, GRIDPAGE_POSTFIX);
  }

  @Override
  public Object expand(GridModel grid, String gridId, String rowId) {
    Objects.nonNull(rowId);
    GridRow row = grid.getPage().getRows().stream()
        .filter(r -> rowId.equals(r.getId()))
        .findFirst()
        .orElse(null);
    if (row == null) {
      log.error("Row not found by id: {}, {}", gridId, rowId);
      return null;
    }
    InvocationRequest request = getCallback(grid.getViewUuid(), gridId, EXPAND_POSTFIX);
    if (request == null) {
      log.warn("Expand handler not found for grid {}", gridId);
    }
    return executeCallback(request, row);
  }

  private void addCallback(UUID viewUuid, String gridId, InvocationRequest request,
      String postfix) {
    View view = viewApi.getView(viewUuid);
    String callbackKey = gridId + postfix;
    view.putParametersItem(callbackKey, request);
  }

  private InvocationRequest getCallback(UUID viewUuid, String gridId, String postfix) {
    if (viewUuid == null || Strings.isNullOrEmpty(gridId)) {
      return null;
    }
    View view = viewApi.getView(viewUuid);
    String expandCallbackKey = gridId + postfix;
    Object requestObject = view.getParameters().get(expandCallbackKey);
    InvocationRequest request = objectApi.asType(InvocationRequest.class, requestObject);
    return request;
  }

  private Object executeCallback(InvocationRequest request, Object parameter) {
    if (request == null) {
      return parameter;
    }
    try {
      request.getParameters().get(0).setValue(parameter);
      InvocationParameter result = invocationApi.invoke(request);
      if (result == null || result.getValue() == null) {
        throw new IllegalArgumentException("Action returned nothing");
      }
      return result.getValue();
    } catch (Exception e) {
      throw new IllegalArgumentException("Action throw an error", e);
    }

  }
}
