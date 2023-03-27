package org.smartbit4all.api.view;

import java.time.OffsetDateTime;
import java.util.Map;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.Session;
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

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Override
  public void fireActionPerformed(View view, UiActionRequest request, String objectName) {
    Session session = sessionManagementApi.readSession(sessionApi.getSessionUri());
    ObjectSerializer serializer = objectApi.getDefaultSerializer();
    Map<String, Object> viewAsMap = serializer.toMap(view);
    View view2 = serializer.fromMap(viewAsMap, View.class);
    invocationApi
        .publisher(ViewPublisherApi.class, ViewSubscriberApi.class,
            ViewPublisherApi.ACTION_PERFORMED)
        .publish(api -> api.actionPerformed(view2, request, objectName,
            session, OffsetDateTime.now()));
  }

}
