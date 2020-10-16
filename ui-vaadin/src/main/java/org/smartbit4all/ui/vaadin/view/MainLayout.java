package org.smartbit4all.ui.vaadin.view;

import org.smartbit4all.ui.vaadin.components.navigation.bar.AppBar;
import org.smartbit4all.ui.vaadin.components.navigation.drawer.NaviDrawer;
import com.vaadin.flow.component.UI;

public interface MainLayout {
  
  public static MainLayout get() {
    return (MainLayout) UI.getCurrent().getChildren()
        .filter(component -> MainLayout.class.isInstance(component)).findFirst()
        .get();
  }

  public AppBar getAppBar();
  
  public NaviDrawer getNaviDrawer();
  
}
