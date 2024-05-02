package org.smartbit4all.api.view;

import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public interface ViewPublisherApi {

  static final String API = "org.smartbit4all.api.view.ViewPublisherApi";

  static final String ACTION_PERFORMED = "actionPerformed";

  static final String VIEW_OPENED = "viewOpened";

  void fireActionPerformed(View view, UiActionRequest request, String objectIdentifier,
      String objectName);

  void fireActionPerformed(View view, UiActionRequest request, String objectIdentifier,
      String objectName,
      Object previousModel, Object nextModel);

  void fireViewOpened(View view, String objectIdentifier,
      String objectName);

}
