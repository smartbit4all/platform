package org.smartbit4all.api.view;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewState;
import org.smartbit4all.api.view.bean.ViewStateUpdate;

public class ViewContexts {

  public static final String VIEW_NOT_FOUND_BY_UUID = "View not found by UUID: ";
  public static final String INITIAL_MODEL = "initialModel";

  private ViewContexts() {}

  public static View getView(ViewContext context, UUID viewUuid) {
    View view = context.getViews().stream()
        .filter(v -> viewUuid.equals(v.getUuid()))
        .findFirst()
        .orElse(null);
    if (view == null) {
      // check closed children views, where model is kept
      view = getViewsIncludingClosedChildren(context.getViews())
          .filter(v -> v != null && v.getModel() != null)
          .filter(v -> viewUuid.equals(v.getUuid()))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException(VIEW_NOT_FOUND_BY_UUID + viewUuid));
    }
    return view;
  }

  private static Stream<View> getViewsIncludingClosedChildren(List<View> views) {
    return Stream.concat(
        views.stream(),
        views.stream()
            .flatMap(v -> getViewsIncludingClosedChildren(v.getClosedChildrenViews())));
  }

  public static ViewContext updateViewState(ViewContext context, ViewStateUpdate update) {
    return updateViewState(context, update.getUuid(), update.getState());
  }

  public static ViewContext updateViewState(ViewContext context, UUID viewUuid, ViewState state) {
    return updateViewData(context, viewUuid, view -> view.state(state));
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
