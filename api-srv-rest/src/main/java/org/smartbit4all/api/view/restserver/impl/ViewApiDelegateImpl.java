package org.smartbit4all.api.view.restserver.impl;

import java.util.UUID;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.bean.MessageResult;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
import org.smartbit4all.api.view.restserver.ViewApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class ViewApiDelegateImpl implements ViewApiDelegate {

  private static final ViewConstraint EMPTY_CONSTRAINT = new ViewConstraint();

  @Autowired
  private ViewContextService viewContextService;

  @Autowired
  private ViewApi viewApi;

  @Override
  public ResponseEntity<ViewContext> createViewContext() throws Exception {
    return ResponseEntity.ok(viewContextService.createViewContext());
  }

  @Override
  public ResponseEntity<ViewContext> updateViewContext(ViewContextUpdate viewContextUpdate)
      throws Exception {
    viewContextService.updateViewContext(viewContextUpdate);
    return getViewContext(viewContextUpdate.getUuid());
  }

  @Override
  public ResponseEntity<ViewContext> getViewContext(UUID uuid) throws Exception {
    return ResponseEntity.ok(viewContextService.getViewContext(uuid));
  }

  @Override
  public ResponseEntity<Void> message(UUID viewUuid, UUID messageUuid, MessageResult messageResult)
      throws Exception {
    viewContextService.handleMessage(viewUuid, messageUuid, messageResult);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<ViewConstraint> getViewConstraint(UUID uuid) throws Exception {
    ViewConstraint constraint = viewApi.getView(uuid).getConstraint();
    return ResponseEntity.ok(constraint == null ? EMPTY_CONSTRAINT : constraint);
  }
}
