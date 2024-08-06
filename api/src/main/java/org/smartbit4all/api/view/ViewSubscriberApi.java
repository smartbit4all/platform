package org.smartbit4all.api.view;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.view.bean.DeviceInfo;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public interface ViewSubscriberApi {

  void actionPerformed(View view, UiActionRequest uiActionRequest, String objectIdentifier,
      String objectName,
      Session session, OffsetDateTime timestamp, Object previousModel, Object nextModel);

  void fireViewOpened(URI sessionUri, URI userUri, OffsetDateTime timestamp,
      View view, String objectIdentifier, String objectName);

  void fireDeviceInfoChanged(DeviceInfo deviceInfo, Session session, UUID viewContextUuid,
      OffsetDateTime timestamp);

  void onActionExecuted(
      View view,
      UiActionRequest request,
      String widgetId,
      String nodeId,
      Map<String, Object> viewContextBefore,
      Map<String, Object> viewContextAfter,
      Session session,
      OffsetDateTime timestamp);

}
