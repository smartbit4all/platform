package org.smartbit4all.api.view;

import java.util.UUID;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewData;
import org.smartbit4all.api.view.bean.ViewState;
import org.smartbit4all.api.view.bean.ViewStateUpdate;

public class ViewContexts {

  private ViewContexts() {}

  public static ViewData getView(ViewContext context, UUID viewUuid) {
    return context.getViews().stream()
        .filter(v -> viewUuid.equals(v.getUuid()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("View not found by UUID: " + viewUuid));
  }

  public static ViewContext updateViewState(ViewContext context, ViewStateUpdate update) {
    return updateViewState(context, update.getUuid(), update.getState());
  }

  public static ViewContext updateViewState(ViewContext context, UUID viewUuid, ViewState state) {
    context.getViews().stream()
        .filter(v -> viewUuid.equals(v.getUuid()))
        .findFirst()
        .ifPresent(view -> view.setState(state));
    return context;
  }

}
