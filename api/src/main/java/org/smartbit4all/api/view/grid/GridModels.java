package org.smartbit4all.api.view.grid;

import static java.util.stream.Collectors.toList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.view.bean.Style;

/**
 * {@link GridModel} static utility method collection
 */
public class GridModels {

  private GridModels() {}

  @SuppressWarnings("null")
  public static Optional<GridRow> findGridRowById(GridModel gridModel, String rowId) {
    Objects.requireNonNull(rowId, "rowId can not be null!");
    Objects.requireNonNull(gridModel, "gridModel can not be null!");
    Objects.requireNonNull(gridModel.getPage(), "gridModel.getPage() can not be null!");
    Objects.requireNonNull(gridModel.getPage().getRows(),
        "gridModel.getPage().getRows() can not be null!");

    return gridModel.getPage().getRows().stream()
        .filter(row -> Objects.equals(row.getId(), rowId)).findFirst();
  }

  @SuppressWarnings("unchecked")
  public static Object getValueFromGridRow(GridModel gridModel, String rowId,
      String property) {
    GridRow row = GridModels
        .findGridRowById(gridModel, rowId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Unable to find the " + rowId + " row."));

    return getValueFromGridRow(row, property);
  }

  public static final Object getValueFromGridRow(GridRow gridRow, String property) {
    Objects.requireNonNull(gridRow);
    return gridRow.getData() instanceof Map
        ? ((Map<String, Object>) gridRow.getData()).get(property)
        : null;
  }

  public static final void setValueFromGridRow(GridRow gridRow, String property, Object value) {
    Objects.requireNonNull(gridRow);
    Objects.requireNonNull(property);
    Map<String, Object> data = gridRow.getData() instanceof Map
        ? ((Map<String, Object>) gridRow.getData())
        : null;
    if (data != null) {
      data.put(property, value);
    }
  }

  public static void hideColumns(GridModel grid, String... columns) {
    hideColumns(grid.getView(), columns);
  }

  public static void hideColumns(GridModel grid, List<String> columns) {
    hideColumns(grid.getView(), columns);
  }

  public static void hideColumns(GridView gridView, String... columns) {
    hideColumns(gridView, Arrays.asList(columns));
  }

  public static void hideColumns(GridView gridView, List<String> columns) {
    List<String> orderedColumns = gridView.getOrderedColumnNames().stream()
        .filter(col -> !columns.contains(col))
        .collect(toList());
    gridView.setOrderedColumnNames(orderedColumns);

    gridView.getDescriptor().getColumns().stream()
        .filter(col -> columns.contains(col.getPropertyName()))
        .forEach(col -> col.setAlwaysHidden(true));
  }

  public static void hideColumnsLabel(GridModel grid, String... columns) {
    hideColumnsLabel(grid.getView(), columns);
  }

  public static void hideColumnsLabel(GridModel grid, List<String> columns) {
    hideColumnsLabel(grid.getView(), columns);
  }

  public static void hideColumnsLabel(GridView gridView, String... columns) {
    hideColumnsLabel(gridView, Arrays.asList(columns));
  }

  public static void hideColumnsLabel(GridView gridView, List<String> columns) {
    List<String> orderedColumns = gridView.getOrderedColumnNames().stream()
        .filter(col -> !columns.contains(col))
        .collect(toList());
    gridView.setOrderedColumnNames(orderedColumns);

    gridView.getDescriptor().getColumns().stream()
        .filter(col -> columns.contains(col.getPropertyName()))
        .forEach(col -> col.setHideLabel(true));
  }

  public static void setColumnType(GridModel grid, String column, Class<?> typeClass) {
    setColumnType(grid, column, typeClass.getName());
  }

  public static void setColumnType(GridModel grid, String column, String typeClass) {
    setColumnType(grid, column, typeClass, null);
  }

  public static void setColumnType(GridModel grid, String column, Class<?> typeClass,
      String typeFormat) {
    setColumnType(grid, column, typeClass.getName(), typeFormat);
  }

  public static void setColumnType(GridModel grid, String column, String typeClass,
      String typeFormat) {
    grid.getView().getDescriptor().getColumns().stream()
        .filter(col -> column.equals(col.getPropertyName()))
        .forEach(col -> col
            .typeClass(typeClass)
            .typeFormat(typeFormat));
  }

  public static void setColumnStyle(GridModel grid, String column, Style style) {
    grid.getView().getDescriptor().getColumns().stream()
        .filter(col -> column.equals(col.getPropertyName()))
        .forEach(col -> col
            .style(style));
  }
}
