package org.smartbit4all.ui.common.action;

import java.util.List;

public interface NavigationActionListener {
  
  void onShowActions(List<Action> actions);

  void onHideActions(List<Action> actions);

}
