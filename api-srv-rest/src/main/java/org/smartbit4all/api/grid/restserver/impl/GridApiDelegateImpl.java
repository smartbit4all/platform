package org.smartbit4all.api.grid.restserver.impl;

import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridUpdateData;
import org.smartbit4all.api.grid.restserver.GridApiDelegate;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class GridApiDelegateImpl implements GridApiDelegate {

  @Autowired
  private GridModelApi gridModelApi;

  @Override
  public ResponseEntity<GridModel> load(UUID uuid, String gridIdentifier) throws Exception {
    return ResponseEntity.ok(
        gridModelApi.executeGridCall(uuid, gridIdentifier, grid -> grid));
  }

  @Override
  public ResponseEntity<Void> setPage(UUID uuid, String gridIdentifier, String offsetStr,
      String limitStr)
      throws Exception {
    try {
      int offset = Integer.valueOf(offsetStr);
      int limit = Integer.valueOf(limitStr);
      gridModelApi.executeGridCall(uuid, gridIdentifier,
          grid -> gridModelApi.loadPage(grid, offset, limit));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid offset or limit");
    }
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> performAction(UUID uuid, String gridIdentifier,
      String actionIdentifier) throws Exception {
    // TODO Auto-generated method stub
    return GridApiDelegate.super.performAction(uuid, gridIdentifier, actionIdentifier);
  }

  @Override
  public ResponseEntity<GridModel> update(UUID uuid, String gridIdentifier,
      GridUpdateData gridUpdateData) throws Exception {
    return ResponseEntity.ok(
        gridModelApi.executeGridCall(uuid, gridIdentifier,
            grid -> gridModelApi.updateGrid(grid, gridUpdateData)));
  }

}
