package org.smartbit4all.api.view.restserver.impl;

import java.util.UUID;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.restserver.ViewApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class ViewApiDelegateImpl implements ViewApiDelegate {

  @Autowired
  private ViewContextService viewContextService;

  @Override
  public ResponseEntity<ViewContext> getViewContext(UUID uuid) throws Exception {
    return ResponseEntity.ok(viewContextService.getViewContext(uuid));
  }

  @Override
  public ResponseEntity<ViewContext> updateViewContext(UUID uuid, ViewContext context)
      throws Exception {
    viewContextService.updateViewContext(uuid, c -> context);
    return getViewContext(uuid);
  }

}
