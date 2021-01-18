package org.smartbit4all.ui.common.toolbar;

import java.util.List;
import org.smartbit4all.ui.common.action.Action;

public class ToolBarControllerImpl implements ToolBarController {

  private String name;

  protected ToolBarView view;
  
  public ToolBarControllerImpl(String name) {
    this.name = name;
  }

  @Override
  public void setUI(ToolBarView view) {
    this.view = view;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public void cleatToolBar() {
    view.clearActions();
  }

  @Override
  public void showActions(List<Action> actions) {
    for (Action action : actions) {
      view.addAction(action);
    }
    
  }

  @Override
  public void hideActions(List<Action> actions) {
    for (Action action : actions) {
      view.removeAction(action);
    }    
  }
}
