package org.smartbit4all.api.view.grid;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridRow;

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

    return row.getData() instanceof Map
        ? ((Map<String, Object>) row.getData()).get(property)
        : null;
  }



}
