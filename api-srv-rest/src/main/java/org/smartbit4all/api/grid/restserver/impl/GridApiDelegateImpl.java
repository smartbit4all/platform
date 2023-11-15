package org.smartbit4all.api.grid.restserver.impl;

import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridUpdateData;
import org.smartbit4all.api.grid.restserver.GridApiDelegate;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.bean.ViewContextChange;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class GridApiDelegateImpl implements GridApiDelegate {

  @Autowired
  private GridModelApi gridModelApi;

  @Autowired
  private ViewContextService viewContextService;

  @Override
  public ResponseEntity<GridModel> load(UUID uuid, String gridIdentifier) throws Exception {
    return ResponseEntity.ok(
        gridModelApi.executeGridCall(uuid, gridIdentifier, grid -> grid));
  }

  @Override
  public ResponseEntity<Void> setPage(UUID uuid, String gridId, String offsetStr,
      String limitStr)
      throws Exception {
    try {
      int offset = Integer.parseInt(offsetStr);
      int limit = Integer.parseInt(limitStr);
      gridModelApi.loadPage(uuid, gridId, offset, limit);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid offset or limit");
    }
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<GridModel> update(UUID uuid, String gridId,
      GridUpdateData gridUpdateData) throws Exception {
    return ResponseEntity.ok(gridModelApi.updateGrid(uuid, gridId, gridUpdateData));
  }

  @Override
  public ResponseEntity<Object> expand(UUID uuid, String gridId, String rowId) throws Exception {
    return ResponseEntity.ok(
        gridModelApi.executeGridCall(uuid, gridId,
            grid -> gridModelApi.expand(grid, gridId, rowId)));
  }

  @Override
  public ResponseEntity<ViewContextChange> select(UUID uuid, String gridId, String rowId,
      Boolean selected)
      throws Exception {
    return ResponseEntity.ok(viewContextService.performViewCall(
        () -> {
          gridModelApi.selectRow(
              uuid, gridId, rowId, Boolean.TRUE == selected);
          return null;
        }, "gridSelectRow"));
  }

  @Override
  public ResponseEntity<ViewContextChange> selectAll(UUID uuid, String gridId, Boolean selected)
      throws Exception {
    return ResponseEntity.ok(viewContextService.performViewCall(
        () -> {
          gridModelApi.selectAllRow(uuid, gridId, Boolean.TRUE == selected);
          return null;
        },
        "gridSelectAllRow"));
  }

}
