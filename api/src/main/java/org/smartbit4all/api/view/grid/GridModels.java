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
  public static Object getValueFromGridRow(GridModel categoryGridModel, String rowId,
      String property) {
    GridRow row = GridModels.findGridRowById(categoryGridModel, rowId)
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

  public static void hideColumns(GridModel grid, String... columns) {
    hideColumns(grid, Arrays.asList(columns));
  }

  public static void hideColumns(GridModel grid, List<String> columns) {

    GridView gridView = grid.getView();
    List<String> orderedColumns = gridView.getOrderedColumnNames().stream()
        .filter(col -> !columns.contains(col))
        .collect(toList());
    gridView.setOrderedColumnNames(orderedColumns);

    gridView.getDescriptor().getColumns().stream()
        .filter(col -> columns.contains(col.getPropertyName()))
        .forEach(col -> col.setAlwaysHidden(true));
  }
}
