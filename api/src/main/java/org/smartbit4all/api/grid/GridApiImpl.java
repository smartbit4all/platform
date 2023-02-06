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
import org.smartbit4all.api.grid.bean.GridColumnMeta;
import org.smartbit4all.api.grid.bean.GridContentPage;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridViewOption;
import org.smartbit4all.api.grid.bean.GridViewOption.KindEnum;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.DataColumn;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.springframework.beans.factory.annotation.Autowired;

public class GridApiImpl implements GridApi {

  @Autowired
  private ObjectApi objectApi;

  @Override
  public <T> GridModel modelOf(Class<T> clazz, List<T> objectList, Map<String, String> columns) {
    GridModel result = new GridModel();

    GridViewOption tableHeader = new GridViewOption().kind(KindEnum.TABLE);
    result.addViewOptionsItem(tableHeader);
    columns.entrySet().stream().forEach(e -> tableHeader
        .addColumnsItem(new GridColumnMeta().label(e.getValue()).propertyName(e.getKey())));

    AtomicInteger i = new AtomicInteger(1);
    GridContentPage page = new GridContentPage().lowerBound(1).upperBound(objectList.size())
        .totalRowCount(objectList.size());

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
    GridViewOption tableHeader = new GridViewOption().kind(KindEnum.TABLE);
    result.addViewOptionsItem(tableHeader);
    for (DataColumn<?> column : tableData.columns()) {
      tableHeader.addColumnsItem(
          new GridColumnMeta().propertyName(column.getName())
              .typeClass(column.getProperty().type().getName()));
    }

    result.page(new GridContentPage().lowerBound(1).upperBound(tableData.size())
        .totalRowCount(tableData.size()));
    for (DataRow row : tableData.rows()) {
      result.getPage().addRowsItem(new GridRow().data(tableData.columns().stream()
          .filter(c -> tableData.get(c, row) != null)
          .collect(toMap(DataColumn::getName, c -> tableData.get(c, row)))));
    }
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

}
