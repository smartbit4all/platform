package org.smartbit4all.api.view.grid;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.grid.bean.GridColumnMeta;
import org.smartbit4all.api.grid.bean.GridDataAccessConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.grid.bean.GridViewDescriptor;
import org.smartbit4all.api.grid.bean.GridViewDescriptor.KindEnum;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
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
  public <T> GridModel modelOf(Class<T> clazz, List<T> objectList, Map<String, String> columns) {
    GridModel result = new GridModel();
    result.setTotalRowCount(objectList.size());

    GridViewDescriptor tableHeader = new GridViewDescriptor().kind(KindEnum.TABLE);
    GridView tableView = new GridView().descriptor(tableHeader);
    result.addAvailableViewsItem(tableView);
    result.setView(tableView);
    columns.entrySet().stream().forEach(e -> tableHeader
        .addColumnsItem(new GridColumnMeta().label(localeSettingApi.get(e.getValue()))
            .propertyName(e.getKey())));

    // AtomicInteger i = new AtomicInteger(1);
    GridPage page = new GridPage().lowerBound(0).upperBound(objectList.size());
    List<GridRow> gridRows = IntStream.rangeClosed(0, page.getUpperBound())
        .mapToObj(i -> new GridRow()
            .id(String.valueOf(i))
            .data(objectApi.create(null, objectList.get(i))
                .getObjectAsMap()
                .entrySet().stream()
                .filter(e -> columns.containsKey(e.getKey()))
                .collect(toMap(Entry::getKey, Entry::getValue))))
        .collect(toList());
    page.setRows(gridRows);

    result.setPage(page);
    return result;
  }

  @Override
  public GridModel modelOf(TableData<?> tableData) {
    return constructModel(tableData, 0, tableData.size());
  }

  @Override
  public GridModel modelOf(TableData<?> tableData, int lowerBound, int upperBound) {
    return constructModel(tableData, lowerBound, upperBound);
  }

  private GridModel constructModel(TableData<?> tableData, int lowerBound, int upperBound) {
    if (tableData.getUri() == null) {
      tableDataApi.save(tableData);
    }
    GridModel result = new GridModel()
        .accessConfig(new GridDataAccessConfig().dataUri(tableData.getUri()));
    GridViewDescriptor tableHeader = new GridViewDescriptor().kind(KindEnum.TABLE);
    GridView tableView = new GridView().descriptor(tableHeader);
    result.addAvailableViewsItem(tableView);
    result.setView(tableView);
    for (DataColumn<?> column : tableData.columns()) {
      tableHeader.addColumnsItem(
          new GridColumnMeta().propertyName(column.getName())
              .label(localeSettingApi.get(column.getName()))
              .typeClass(column.getProperty().type().getName()));
    }
    result.totalRowCount(tableData.size());

    result.page(
        constructPage(tableData, lowerBound, upperBound).lowerBound(lowerBound)
            .upperBound(upperBound));

    return result;
  }

  @Override
  public GridModel modelOfUris(SearchIndex<?> searchIndex, Stream<URI> uris) {
    TableData<?> tableData = searchIndex.tableDataOfUris(uris);
    return constructModel(tableData, 0, tableData.size());
  }

  @Override
  public <T> GridModel modelOfObjects(SearchIndex<T> searchIndex, Stream<T> objects) {
    TableData<?> tableData = searchIndex.tableDataOfObjects(objects);
    return constructModel(tableData, 0, tableData.size());
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
