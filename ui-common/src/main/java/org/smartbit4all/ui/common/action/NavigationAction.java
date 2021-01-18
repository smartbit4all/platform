package org.smartbit4all.ui.common.action;

import org.smartbit4all.ui.common.navigation.NavigationController;

public class NavigationAction extends Action {
  
  protected NavigationController navigationController;

  public NavigationAction(String name) {
    super(name);
  }
  
  public void setNavigationController(NavigationController navigationController) {
    this.navigationController = navigationController;
  }
}
