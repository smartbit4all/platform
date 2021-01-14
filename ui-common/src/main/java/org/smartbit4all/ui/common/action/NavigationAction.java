package org.smartbit4all.ui.common.action;

import org.smartbit4all.ui.common.navigation.NavigationController;
import org.springframework.beans.factory.annotation.Autowired;

public class NavigationAction extends Action {
  
  @Autowired
  protected NavigationController navigationController;

  public NavigationAction(String name) {
    super(name);
  }
}
