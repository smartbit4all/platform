package org.smartbit4all.ui.common.toolbar;

import java.util.List;
import org.smartbit4all.ui.common.action.Action;
import org.smartbit4all.ui.common.controller.UIController;

public interface ToolBarController extends UIController{
  
  void setUI(ToolBarView view);
  
  void showActions(List<Action> actions);
  
  void hideActions(List<Action> actions);

  String name();

  void cleatToolBar();
  
}
