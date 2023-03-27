package org.smartbit4all.api.view;

import java.util.UUID;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewState;
import org.smartbit4all.api.view.bean.ViewStateUpdate;

public class ViewContexts {

  public static final String INITIAL_MODEL = "initialModel";

  private ViewContexts() {}

  public static View getView(ViewContext context, UUID viewUuid) {
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

  public static ViewContext updateViewData(ViewContext context, UUID viewUuid,
      UnaryOperator<View> update) {
    context.getViews().stream()
        .filter(v -> viewUuid.equals(v.getUuid()))
        .findFirst()
        .ifPresent(update::apply);
    return context;
  }

}
