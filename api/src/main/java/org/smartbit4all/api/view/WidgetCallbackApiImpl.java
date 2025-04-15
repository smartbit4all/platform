package org.smartbit4all.api.view;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.google.common.base.Strings;

public class WidgetCallbackApiImpl implements WidgetCallbackApi {

  @Autowired(required = false) // FIXME: This is bad and ConditionalOnBean cannot work!
  private ViewApi viewApi;
  @Autowired
  private InvocationApi invocationApi;

  @Override
  public void setCallback(UUID viewUuid, String widgetId, InvocationRequest request,
      String postfix) {
    viewApi.setCallback(viewUuid, widgetId + postfix, request);
  }

  @Override
  public void addCallback(UUID viewUuid, String widgetId, InvocationRequest request,
      String postfix) {
    viewApi.addCallback(viewUuid, widgetId + postfix, request);
  }

  @Override
  public InvocationRequest getCallback(UUID viewUuid, String widgetId, String postfix) {
    if (viewUuid == null || Strings.isNullOrEmpty(widgetId)) {
      return null;
    }
    return viewApi.getCallback(viewUuid, widgetId + postfix);
  }

  @Override
  public List<InvocationRequest> getCallbacks(UUID viewUuid, String widgetId, String postfix) {
    if (viewUuid == null || Strings.isNullOrEmpty(widgetId)) {
      return Collections.emptyList();
    }
    return viewApi.getCallbacks(viewUuid, widgetId + postfix);
  }

  @Override
  public void clearCallbacks(UUID viewUuid) {
    viewApi.clearCallbacks(viewUuid);
  }

  @Override
  public Object executeObjectCallback(InvocationRequest request, Object parameter,
      Object... parameters) {
    if (request == null) {
      return parameter;
    }
    try {
      request.getParameters().get(0).setValue(parameter);

      if (parameters != null && !ObjectUtils.isEmpty(parameters)) {
        for (int i = 1; i < parameters.length + 1; i++) {
          request.getParameters().get(i).setValue(parameters[i - 1]);
        }
      }
      InvocationParameter result = invocationApi.invoke(request);
      if (result == null || result.getValue() == null) {
        throw new IllegalArgumentException("Action returned nothing");
      }
      return result.getValue();
    } catch (Exception e) {
      throw new IllegalArgumentException("Action throw an error", e);
    } finally {
      if (request.getParameters() != null && !request.getParameters().isEmpty()) {
        request.getParameters().get(0).setValue(null);
      }
    }
  }



  @Override
  public Object executeObjectCallbacks(List<InvocationRequest> requests, Object parameter,
      Object... parameters) {
    for (InvocationRequest request : requests) {
      parameter = executeObjectCallback(request, parameter, parameters);
    }
    return parameter;
  }

  @Override
  public Object executeObjectCallbacks(List<InvocationRequest> requests, Object parameter) {
    return executeObjectCallbacks(requests, parameter, null);
  }

  @Override
  public void executeVoidCallback(InvocationRequest request, Object... parameters) {
    if (request == null) {
      return;
    }
    try {
      if (parameters != null && parameters.length > 0) {
        for (int i = 0; i < parameters.length; i++) {
          request.getParameters().get(i).setValue(parameters[i]);
        }
      }
      invocationApi.invoke(request);
    } catch (Exception e) {
      throw new IllegalArgumentException("Action throw an error", e);
    } finally {
      if (request.getParameters() != null && parameters != null && parameters.length > 0) {
        for (int i = 0; i < parameters.length; i++) {
          if (request.getParameters().size() > i) {
            request.getParameters().get(i).setValue(null);
          }
        }
      }
    }
  }

  @Override
  public void executeVoidCallbacks(List<InvocationRequest> requests, Object... parameters) {
    for (InvocationRequest request : requests) {
      executeVoidCallback(request, parameters);
    }
  }

}
