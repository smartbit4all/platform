package org.smartbit4all.api.view;

import java.time.OffsetDateTime;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public interface ViewSubscriberApi {

  static final String MODEL_PREV = "modelPrev";
  static final String MODEL_NEXT = "modelNext";

  void actionPerformed(View view, UiActionRequest uiActionRequest, String objectName,
      Session session, OffsetDateTime timestamp);

}
