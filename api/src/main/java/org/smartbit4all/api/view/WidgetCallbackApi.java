package org.smartbit4all.api.view;

import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

public interface WidgetCallbackApi {

  void executeVoidCallback(InvocationRequest request, Object... parameters);

  void setCallback(UUID viewUuid, String widgetId, InvocationRequest request, String postfix);

  void addCallback(UUID viewUuid, String widgetId, InvocationRequest request, String postfix);

  InvocationRequest getCallback(UUID viewUuid, String widgetId, String postfix);

  List<InvocationRequest> getCallbacks(UUID viewUuid, String widgetId, String postfix);

  void clearCallbacks(UUID viewUuid, String requestId);

  Object executeObjectCallback(InvocationRequest request, Object parameter, Object... parameters);

  Object executeObjectCallbacks(List<InvocationRequest> requests, Object parameter);

  Object executeObjectCallbacks(List<InvocationRequest> requests, Object parameter,
      Object... parameters);

  void executeVoidCallbacks(List<InvocationRequest> requests, Object... parameters);

}
