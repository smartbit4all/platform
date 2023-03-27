package org.smartbit4all.api.view;

import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public interface ViewPublisherApi {

  static final String API = "org.smartbit4all.api.view.ViewPublisherApi";

  static final String ACTION_PERFORMED = "actionPerformed";

  void fireActionPerformed(View view, UiActionRequest request);

}
