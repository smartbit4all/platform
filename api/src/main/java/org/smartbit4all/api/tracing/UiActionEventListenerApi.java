package org.smartbit4all.api.tracing;

import org.smartbit4all.api.invocation.EventSubscription;
import org.smartbit4all.api.tracing.bean.UiActionExecutionEvent;
import org.smartbit4all.api.view.ViewPublisherApi;

public interface UiActionEventListenerApi {

  String CHANNEL = "uiActionExecutionListeningChannel";

  @EventSubscription(
      api = ViewPublisherApi.API,
      event = ViewPublisherApi.ACTION_EXECUTED,
      channel = CHANNEL)
  void onActionExecuted(UiActionExecutionEvent e);

}
