package org.smartbit4all.api.view;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.tracing.UiActionEventListenerApi;
import org.smartbit4all.api.tracing.bean.UiActionExecutionEvent;
import org.smartbit4all.api.view.bean.DeviceInfo;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectSerializer;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewPublisherApiImpl implements ViewPublisherApi {

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private InvocationApi invocationApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Autowired(required = false)
  private SessionManagementApi sessionManagementApi;

  @Override
  public void fireActionPerformed(View view, UiActionRequest request, String objectIdentifier,
      String objectName) {
    fireActionPerformed(view, request, objectIdentifier, objectName, null, null);
  }

  @Override
  public void fireActionPerformed(View view, UiActionRequest request, String objectIdentifier,
      String objectName,
      Object previousModel, Object nextModel) {
    if (sessionApi == null || sessionManagementApi == null) {
      return;
    }
    Session session = sessionManagementApi.readSession(sessionApi.getSessionUri());
    ObjectSerializer serializer = objectApi.getDefaultSerializer();
    Map<String, Object> viewAsMap = serializer.toMap(view);
    View view2 = serializer.fromMap(viewAsMap, View.class);
    invocationApi
        .publisher(ViewPublisherApi.class, ViewSubscriberApi.class,
            ViewPublisherApi.ACTION_PERFORMED)
        .publish(api -> api.actionPerformed(view2, request, objectIdentifier, objectName,
            session, OffsetDateTime.now(), previousModel, nextModel));
  }

  @Override
  public void fireViewOpened(View view, String objectIdentifier,
      String objectName) {
    if (sessionApi == null || sessionManagementApi == null) {
      return;
    }
    invocationApi
        .publisher(ViewPublisherApi.class, ViewSubscriberApi.class,
            ViewPublisherApi.VIEW_OPENED)
        .publish(api -> {
          ObjectSerializer serializer = objectApi.getDefaultSerializer();
          Map<String, Object> viewAsMap = serializer.toMap(view);
          View view2 = serializer.fromMap(viewAsMap, View.class);
          api.fireViewOpened(sessionApi.getSessionUri(), sessionApi.getUserUri(),
              OffsetDateTime.now(),
              view2, objectIdentifier, objectName);
        });
  }

  @Override
  public void fireDeviceInfoChanged(UUID viewContextUuid, DeviceInfo deviceInfo) {
    Objects.requireNonNull(viewContextUuid, "viewContextUuid can not be null!");
    Objects.requireNonNull(deviceInfo, "deviceInfo can not be null!");

    if (sessionApi == null || sessionManagementApi == null) {
      return;
    }
    invocationApi
        .publisher(ViewPublisherApi.class, ViewSubscriberApi.class,
            ViewPublisherApi.DEVICE_INFO_CHANGED)
        .publish(api -> {
          Session session = sessionManagementApi.readSession(sessionApi.getSessionUri());
          api.fireDeviceInfoChanged(deviceInfo, session, viewContextUuid, OffsetDateTime.now());
        });
  }

  @Override
  public void fireActionExecuted(
      View view,
      UiActionRequest request,
      String widgetId,
      String nodeId,
      Map<String, Object> viewContextBefore,
      Map<String, Object> viewContextAfter) {
    if (sessionApi == null || sessionManagementApi == null) {
      return;
    }

    final ObjectSerializer serializer = objectApi.getDefaultSerializer();
    final Map<String, Object> viewAsMap = serializer.toMap(view);
    final View viewCopy = serializer.fromMap(viewAsMap, View.class);
    invocationApi
        .publisher(
            ViewPublisherApi.class,
            UiActionEventListenerApi.class,
            ViewPublisherApi.ACTION_EXECUTED)
        .publish(api -> {
          final Session session = sessionManagementApi.readSession(sessionApi.getSessionUri());
          api.onActionExecuted(new UiActionExecutionEvent()
              .view(viewCopy)
              .request(request)
              .widgetId(widgetId)
              .nodeId(nodeId)
              .viewContextBefore(viewContextBefore)
              .viewContextAfter(viewContextAfter)
              .session(session)
              .timestamp(OffsetDateTime.now()));
        });
  }

}
