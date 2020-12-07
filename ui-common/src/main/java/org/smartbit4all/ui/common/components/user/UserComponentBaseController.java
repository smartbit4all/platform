package org.smartbit4all.ui.common.components.user;

import org.smartbit4all.ui.common.controller.UIController;

public interface UserComponentBaseController extends UIController {

  void setUi(UserComponentBaseView ui);

  void navigate(String route);

}
