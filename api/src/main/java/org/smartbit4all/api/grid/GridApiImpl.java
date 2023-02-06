package org.smartbit4all.api.grid;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.grid.bean.GridColumnMeta;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.grid.bean.GridViewDescriptor;
import org.smartbit4all.api.grid.bean.GridViewDescriptor.KindEnum;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.springframework.beans.factory.annotation.Autowired;

public class GridApiImpl implements GridApi {

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private TableDataApi tableDataApi;

  @Override
  public <T> GridModel modelOf(Class<T> clazz, List<T> objectList, Map<String, String> columns) {
    GridModel result = new GridModel();
    result.setTotalRowCount(objectList.size());

    GridViewDescriptor tableHeader = new GridViewDescriptor().kind(KindEnum.TABLE);
    GridView tableView = new GridView().descriptor(tableHeader);
    result.addAvailableViewsItem(tableView);
    result.setView(tableView);
    columns.entrySet().stream().forEach(e -> tableHeader
        .addColumnsItem(new GridColumnMeta().label(e.getValue()).propertyName(e.getKey())));

    AtomicInteger i = new AtomicInteger(1);
    GridPage page = new GridPage().lowerBound(1).upperBound(objectList.size());

    page.rows(objectList.stream().map(o -> new GridRow().id(Integer.toString(i.getAndIncrement()))
        .data(objectApi.create(null, o).getObjectAsMap().entrySet().stream()
            .filter(e -> columns.containsKey(e.getKey()))
            .collect(toMap(Entry::getKey, Entry::getValue))))
        .collect(toList()));

    return result;
  }

  @Override
  public GridModel modelOf(TableData<?> tableData) {
    return constructModel(tableData);
  }

  private GridModel constructModel(TableData<?> tableData) {
    GridModel result = new GridModel();
    GridViewDescriptor tableHeader = new GridViewDescriptor().kind(KindEnum.TABLE);
    GridView tableView = new GridView().descriptor(tableHeader);
    result.addAvailableViewsItem(tableView);
    result.setView(tableView);
    for (DataColumn<?> column : tableData.columns()) {
      tableHeader.addColumnsItem(
          new GridColumnMeta().propertyName(column.getName())
              .typeClass(column.getProperty().type().getName()));
    }
    result.totalRowCount(tableData.size());

    result.page(
        constructPage(tableData, 0, tableData.size()).lowerBound(1).upperBound(tableData.size()));

    return result;
  }

  @Override
  public GridModel modelOfUris(SearchIndex<?> searchIndex, Stream<URI> uris) {
    return constructModel(searchIndex.tableDataOfUris(uris));
  }

  @Override
  public <T> GridModel modelOfObjects(SearchIndex<T> searchIndex, Stream<T> objects) {
    return constructModel(searchIndex.tableDataOfObjects(objects));
  }

  @Override
  public void loadPage(GridModel model, int lowerBound, int upperBound) {
    TableData<?> tableData =
        tableDataApi.readPage(model.getAccessConfig().getDataUri(), lowerBound - 1, upperBound);
    model.page(constructPage(
        tableData, 0, tableData.size())
            .lowerBound(lowerBound).upperBound(upperBound));
  }

  private GridPage constructPage(TableData<?> tableData, int beginIndex, int endIndex) {
    GridPage page = new GridPage();
    for (int i = beginIndex; i < tableData.rows().size(); i++) {
      DataRow row = tableData.rows().get(i);
      page.addRowsItem(new GridRow().data(tableData.columns().stream()
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

}
