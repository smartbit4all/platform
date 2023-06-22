package org.smartbit4all.api.view.grid;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.ToIntFunction;
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
import org.smartbit4all.api.grid.bean.GridSelectionMode;
import org.smartbit4all.api.grid.bean.GridServerModel;
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

  private static final String SERVER_MODEL_POSTFIX = "_serverModel";

  private static final String EXPAND_POSTFIX = "_expandCallback";

  private static final String GRIDROW_POSTFIX = "_gridrowCallback";

  private static final String GRIDPAGE_POSTFIX = "_gridpageCallback";

  private static final String SELECTION_CHANGE_POSTFIX = "_selectionChangeCallback";

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
    viewApi.setWidgetModelInView(GridServerModel.class, viewUuid, gridId + SERVER_MODEL_POSTFIX,
        new GridServerModel());
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
  public void setData(UUID viewUuid, String gridId, TableData<?> data) {
    setData(viewUuid, gridId, data, false);
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
  public void setData(UUID viewUuid, String gridId, TableData<?> data, boolean ignoreOrderByList) {
    executeGridCall(viewUuid, gridId, gridModel -> {
      Integer pageSize = gridModel.getPageSize();
      if (pageSize == null) {
        pageSize = getDefaultPageSize();
      }
      if (!ignoreOrderByList) {
        List<FilterExpressionOrderBy> orderByList = gridModel.getView().getOrderByList();
        if (orderByList != null && !orderByList.isEmpty()) {
          TableDatas.sortByFilterExpression(data, orderByList);
          tableDataApi.save(data);
        }
      }
      if (data.getUri() == null) {
        tableDataApi.save(data);
      }
      if (gridModel.getAccessConfig() != null
          && gridModel.getAccessConfig().getDataUri() != null
          && gridModel.getPage() != null) {
        gridModel.getAccessConfig().dataUri(data.getUri());
        int lowerBound = gridModel.getPage().getLowerBound();
        int upperBound = gridModel.getPage().getUpperBound();
        if (lowerBound == 0 && upperBound == 0) {
          upperBound = pageSize;
        } else {
          pageSize = upperBound - lowerBound;
        }
        if (data.size() == 0) {
          lowerBound = 0;
          upperBound = 0;
        } else {
          // lowerBound inclusive, upperBound exclusive
          if (lowerBound >= data.size()) {
            lowerBound = data.size() - 1 - ((data.size() - 1) % pageSize);
            upperBound = data.size();
          }
          if (upperBound > data.size()) {
            upperBound = data.size();
          }
        }
        gridModel.page(constructPage(viewUuid, gridId, data, lowerBound, upperBound, true)
            .lowerBound(lowerBound)
            .upperBound(lowerBound + pageSize));
      } else {
        gridModel.accessConfig(new GridDataAccessConfig().dataUri(data.getUri()));
        int firstPageSize = Math.min(pageSize, data.size());
        gridModel
            .page(constructPage(viewUuid, gridId, data, 0, firstPageSize, false)
                .lowerBound(0)
                .upperBound(pageSize));
      }
      gridModel.setPageSize(pageSize);
      gridModel.totalRowCount(data.size());
      return gridModel;
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
        constructPage(null, null, tableData, lowerBound, upperBound, false)
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
      GridViewDescriptor descriptor = model.getView().getDescriptor();
      boolean preserveSelection;
      if (descriptor.getPreserveSelectionOnPageChange() != null) {
        preserveSelection = descriptor.getPreserveSelectionOnPageChange();
      } else {
        // default: NONE, SINGLE -> don't preserve, MULTIPLE -> do preserve
        preserveSelection = descriptor.getSelectionMode() == GridSelectionMode.MULTIPLE;
      }
      model.page(constructPage(viewUuid, gridId, tableData, 0, tableData.size(), preserveSelection)
          .lowerBound(offset)
          .upperBound(offset + limit));
      return model;
    });
  }

  @Override
  public void setPageSize(UUID viewUuid, String gridId,
      ToIntFunction<GridModel> newPageSize) {
    executeGridCall(viewUuid, gridId, model -> {
      if (model.getAccessConfig() == null) {
        return model;
      }
      Integer pageSize = newPageSize.applyAsInt(model);
      model.setPageSize(pageSize);
      TableData<?> tableData =
          tableDataApi.readPage(model.getAccessConfig().getDataUri(),
              model.getPage().getLowerBound(), pageSize);
      model.page(constructPage(viewUuid, gridId, tableData, 0, tableData.size(), true)
          .lowerBound(model.getPage().getLowerBound())
          .upperBound(model.getPage().getLowerBound() + pageSize));
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
              Math.min(data.size(), upperBound), true)
                  .lowerBound(lowerBound)
                  .upperBound(upperBound));
        }
      }
      model.getView().setOrderByList(update.getOrderByList());
      return model;
    });
  }

  private GridPage constructPage(UUID viewUuid, String gridId, TableData<?> tableData,
      int beginIndex, int endIndex, boolean preserveSelection) {
    GridPage page = new GridPage();
    page.rows(new ArrayList<>());
    List<InvocationRequest> gridRowCallbacks = getCallbacks(viewUuid, gridId, GRIDROW_POSTFIX);
    for (int i = beginIndex; i < endIndex; i++) {
      DataRow dataRow = tableData.rows().get(i);
      GridRow gridRow = new GridRow().id(Integer.toString(i)).data(tableData.columns().stream()
          .filter(c -> tableData.get(c, dataRow) != null)
          .collect(toMap(DataColumn::getName, c -> tableData.get(c, dataRow))));
      gridRow = (GridRow) executeObjectCallbacks(gridRowCallbacks, gridRow);
      page.addRowsItem(gridRow);
    }
    List<InvocationRequest> gridPageCallbacks = getCallbacks(viewUuid, gridId, GRIDPAGE_POSTFIX);
    page = (GridPage) executeObjectCallbacks(gridPageCallbacks, page);
    GridServerModel serverModel = getGridServerModel(viewUuid, gridId);
    if (serverModel != null && !preserveSelection) {
      serverModel.getSelectedRows().clear();
    }
    Map<String, GridRow> selectedRows = serverModel == null ? Collections.emptyMap()
        : serverModel.getSelectedRows();
    refreshSelectedRows(viewUuid, gridId, page, selectedRows);
    return page;
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
    setCallback(viewUuid, gridId, request, EXPAND_POSTFIX);
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
    return executeObjectCallback(request, row);
  }

  private void setCallback(UUID viewUuid, String gridId, InvocationRequest request,
      String postfix) {
    viewApi.setCallback(viewUuid, gridId + postfix, request);
  }

  private void addCallback(UUID viewUuid, String gridId, InvocationRequest request,
      String postfix) {
    viewApi.addCallback(viewUuid, gridId + postfix, request);
  }

  private InvocationRequest getCallback(UUID viewUuid, String gridId, String postfix) {
    if (viewUuid == null || Strings.isNullOrEmpty(gridId)) {
      return null;
    }
    return viewApi.getCallback(viewUuid, gridId + postfix);
  }

  private List<InvocationRequest> getCallbacks(UUID viewUuid, String gridId, String postfix) {
    if (viewUuid == null || Strings.isNullOrEmpty(gridId)) {
      return Collections.emptyList();
    }
    return viewApi.getCallbacks(viewUuid, gridId + postfix);
  }

  private Object executeObjectCallback(InvocationRequest request, Object parameter) {
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

  private Object executeObjectCallbacks(List<InvocationRequest> requests, Object parameter) {
    for (InvocationRequest request : requests) {
      parameter = executeObjectCallback(request, parameter);
    }
    return parameter;
  }

  private void executeVoidCallback(InvocationRequest request, Object... parameters) {
    if (request == null) {
      return;
    }
    try {
      if (parameters != null && parameters.length > 0) {
        for (int i = 0; i < parameters.length; i++) {
          request.getParameters().get(i).setValue(parameters[i]);
        }
      }
      invocationApi.invoke(request);
    } catch (Exception e) {
      throw new IllegalArgumentException("Action throw an error", e);
    }
  }

  private void executeVoidCallbacks(List<InvocationRequest> requests, Object... parameters) {
    for (InvocationRequest request : requests) {
      executeVoidCallback(request, parameters);
    }
  }

  @Override
  public void refreshGrid(UUID viewUuid, String gridId) {
    Objects.nonNull(viewUuid);
    Objects.nonNull(gridId);
    GridModel model = getGridModel(viewUuid, gridId);
    if (model != null && model.getPage() != null) {
      int lowerBound = model.getPage().getLowerBound();
      int upperBound = model.getPage().getUpperBound();
      loadPage(viewUuid, gridId, lowerBound, upperBound - lowerBound);
    }
  }

  @Override
  public void selectRow(UUID viewUuid, String gridId, String rowId, boolean selected) {
    Objects.nonNull(viewUuid);
    Objects.nonNull(gridId);
    GridModel model = getGridModel(viewUuid, gridId);
    GridSelectionMode selectionMode = model.getView().getDescriptor().getSelectionMode();
    if (selectionMode == null || selectionMode == GridSelectionMode.NONE) {
      return;
    }
    GridServerModel serverModel = getGridServerModel(viewUuid, gridId);
    if (selected) {
      if (selectionMode == GridSelectionMode.SINGLE) {
        serverModel.getSelectedRows().clear();
      }
      GridRow row = GridModels.findGridRowById(model, rowId)
          .orElseThrow(() -> new IllegalArgumentException("row not found"));
      serverModel.putSelectedRowsItem(rowId, row);
    } else {
      serverModel.getSelectedRows().remove(rowId);
    }
    model.allRowsSelected(Objects.equals(model.getSelectedRowCount(), model.getTotalRowCount()));
    refreshSelectedRows(viewUuid, gridId, model.getPage(), serverModel.getSelectedRows());
  }

  private void refreshSelectedRows(UUID viewUuid, String gridId, GridPage page,
      Map<String, GridRow> selectedRows) {
    selectedRows.values().forEach(row -> row.selected(true));
    for (GridRow row : page.getRows()) {
      row.selected(row.getSelectable() != Boolean.FALSE && selectedRows.containsKey(row.getId()));
    }
    executeVoidCallbacks(getCallbacks(viewUuid, gridId, SELECTION_CHANGE_POSTFIX),
        viewUuid, gridId);
  }

  @Override
  public void selectAllRow(UUID viewUuid, String gridId, boolean selected) {
    Objects.nonNull(viewUuid);
    Objects.nonNull(gridId);
    GridModel model = getGridModel(viewUuid, gridId);
    GridServerModel serverModel = getGridServerModel(viewUuid, gridId);
    model.allRowsSelected(selected);
    if (selected) {
      model.selectedRowCount(model.getTotalRowCount());
      // check if all rows are in page
      if (model.getPage().getRows().size() == model.getTotalRowCount()) {
        // all row is in one page
        serverModel.selectedRows(
            model.getPage().getRows().stream()
                .collect(toMap(GridRow::getId, row -> row)));
      } else if (model.getAccessConfig().getDataUri() != null) {
        // read all rows from tableData, createRows, add to selectedRows
        // TODO
      } else {
        // TODO
      }
    } else {
      model.selectedRowCount(0);
      serverModel.getSelectedRows().clear();
    }
    refreshSelectedRows(viewUuid, gridId, model.getPage(), serverModel.getSelectedRows());
  }

  @Override
  public void addSelectionChangeListener(UUID viewUuid, String gridId, InvocationRequest request) {
    addCallback(viewUuid, gridId, request, SELECTION_CHANGE_POSTFIX);
  }

  @Override
  public List<GridRow> getSelectedRows(UUID viewUuid, String gridId) {
    Objects.nonNull(viewUuid);
    Objects.nonNull(gridId);
    return getGridServerModel(viewUuid, gridId)
        .getSelectedRows()
        .values().stream()
        .collect(toList());
  }

  private GridModel getGridModel(UUID viewUuid, String gridId) {
    if (viewUuid == null) {
      return null;
    }
    return viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
  }

  private GridServerModel getGridServerModel(UUID viewUuid, String gridId) {
    if (viewUuid == null) {
      return null;
    }
    return viewApi.getWidgetModelFromView(GridServerModel.class, viewUuid,
        gridId + SERVER_MODEL_POSTFIX);
  }


}
