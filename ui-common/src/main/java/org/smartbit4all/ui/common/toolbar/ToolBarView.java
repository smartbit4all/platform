package org.smartbit4all.ui.common.toolbar;

import java.util.List;
import org.smartbit4all.ui.common.action.Action;
import org.smartbit4all.ui.common.view.UIView;

public interface ToolBarView extends UIView {

  void setActions(List<Action> actions);
  
  void addAction(Action action);
  
  void removeAction(Action action);

  void clearActions();
}
