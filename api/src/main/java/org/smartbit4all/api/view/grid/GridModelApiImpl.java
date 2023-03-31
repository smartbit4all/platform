package org.smartbit4all.api.view.grid;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
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
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.springframework.beans.factory.annotation.Autowired;

public class GridModelApiImpl implements GridModelApi {

  @Autowired
  private ObjectApi objectApi;

  // FIXME (viewApi is not present everywhere)
  @Autowired(required = false)
  private ViewApi viewApi;

  @Autowired
  private TableDataApi tableDataApi;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public <T> GridModel modelOf(Class<T> clazz, List<T> objectList, Map<String, String> columns,
      String... columnPrefix) {
    return modelOf(clazz, objectList, columns, 0, objectList.size(), columnPrefix);
  }

  @Override
  public <T> GridModel modelOf(Class<T> clazz, List<T> objectList,
      Map<String, String> columns, int lowerBound, int upperBound, String... columnPrefix) {
    return modelOf(
        tableDataApi.tableOf(clazz, objectList, new ArrayList<>(columns.keySet())),
        lowerBound,
        upperBound,
        columnPrefix);
  }

  @Override
  public GridModel modelOf(TableData<?> tableData, String... columnPrefix) {
    return constructModel(tableData, 0, tableData.size(), columnPrefix);
  }

  @Override
  public GridModel modelOf(TableData<?> tableData, int lowerBound, int upperBound,
      String... columnPrefix) {
    return constructModel(tableData, lowerBound, upperBound, columnPrefix);
  }

  private GridModel constructModel(TableData<?> tableData, int lowerBound, int upperBound,
      String... columnPrefix) {
    if (tableData.getUri() == null) {
      tableDataApi.save(tableData);
    }
    GridModel result = new GridModel()
        .accessConfig(new GridDataAccessConfig().dataUri(tableData.getUri()));
    GridViewDescriptor tableHeader = new GridViewDescriptor().kind(KindEnum.TABLE);
    GridView tableView = new GridView().descriptor(tableHeader);
    result.addAvailableViewsItem(tableView);
    result.setView(tableView);
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
    for (DataColumn<?> column : tableData.columns()) {
      keys[idxLastKey] = column.getName();
      tableHeader.addColumnsItem(
          new GridColumnMeta().propertyName(column.getName())
              .label(localeSettingApi.get(keys))
              .typeClass(column.getProperty().type().getName()));
    }
    result.totalRowCount(tableData.size());

    result.page(
        constructPage(tableData, lowerBound, upperBound).lowerBound(lowerBound)
            .upperBound(upperBound));

    return result;
  }

  @Override
  public GridModel modelOfUris(SearchIndex<?> searchIndex, Stream<URI> uris,
      String... columnPrefix) {
    TableData<?> tableData = searchIndex.tableDataOfUris(uris);
    return constructModel(tableData, 0, tableData.size(), columnPrefix);
  }

  @Override
  public <T> GridModel modelOfObjects(SearchIndex<T> searchIndex, Stream<T> objects,
      String... columnPrefix) {
    TableData<?> tableData = searchIndex.tableDataOfObjects(objects);
    return constructModel(tableData, 0, tableData.size(), columnPrefix);
  }

  @Override
  public GridModel loadPage(GridModel model, int offset, int limit) {
    TableData<?> tableData =
        tableDataApi.readPage(model.getAccessConfig().getDataUri(), offset, limit);
    model.page(constructPage(tableData, 0, tableData.size())
        .lowerBound(offset)
        .upperBound(offset + limit));
    return model;
  }

  @Override
  public GridModel updateGrid(GridModel model, GridUpdateData update) {
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
      int offset = model.getPage().getLowerBound();
      int limit = model.getPage().getUpperBound() - offset;
      TableData<?> data =
          tableDataApi.readPage(model.getAccessConfig().getDataUri(), offset, limit);
      TableDatas.sortByFilterExpression(data, update.getOrderByList());
      tableDataApi.save(data);
      model.getAccessConfig().setDataUri(data.getUri());
      model.page(constructPage(data, 0, data.size())
          .lowerBound(offset)
          .upperBound(offset + limit));
    }
    model.getView().setOrderByList(update.getOrderByList());
    return model;
  }

  private GridPage constructPage(TableData<?> tableData, int beginIndex, int endIndex) {
    GridPage page = new GridPage();
    for (int i = beginIndex; i < endIndex; i++) {
      DataRow row = tableData.rows().get(i);
      page.addRowsItem(new GridRow().id(Integer.toString(i)).data(tableData.columns().stream()
          .filter(c -> tableData.get(c, row) != null)
          .collect(toMap(DataColumn::getName, c -> tableData.get(c, row)))));
    }
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
    GridModel gridModel = viewApi.getComponentModelFromView(GridModel.class, viewUuid, gridId);
    gridModel.setViewUuid(viewUuid);
    T result = gridCall.apply(gridModel);
    viewApi.setComponentModelInView(GridModel.class, viewUuid, gridId, gridModel);
    return result;
  }

}
