package org.smartbit4all.ui.vaadin.api;

import java.util.UUID;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.vaadin.components.navigation.Navigation;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;

// TODO inherit from ui-common / UINavigationImpl?
public class UINavigationVaadinRouting extends UINavigationVaadinCommon {

  private Class<? extends RouterLayout> mainLayout;

  public UINavigationVaadinRouting(UI ui, UserSessionApi userSessionApi) {
    super(ui, userSessionApi);
  }

  @Override
  public void navigateTo(NavigationTarget navigationTarget) {

    try {
      ObjectEditing.currentConstructionUUID.set(navigationTarget.getUuid());
      Navigation navigation = Navigation.to(navigationTarget.getViewName());
      if (navigationTarget.getParameters() != null) {
        navigationTarget.getParameters().entrySet().stream()
            .forEach(entry -> navigation.param(entry.getKey(), entry.getValue()));
      }
      navigation.navigate(ui);
    } finally {
      ObjectEditing.currentConstructionUUID.set(null);
    }

  }

  @Override
  public void registerView(NavigableViewDescriptor viewDescriptor) {
    super.registerView(viewDescriptor);
    String viewName = viewDescriptor.getViewName();
    RouteConfiguration configuration = RouteConfiguration.forSessionScope();
    if (!configuration.isPathRegistered(viewName)) {
      if (mainLayout != null) {
        configuration.setRoute(viewName, navigableViewClasses.get(viewName), mainLayout);
      } else {
        configuration.setRoute(viewName, navigableViewClasses.get(viewName));
      }
    }
  }

  @Override
  public void close(UUID navigationTargetUuid) {
    // TODO Auto-generated method stub

  }

  public void setMainLayout(Class<? extends RouterLayout> mainLayout) {
    if (this.mainLayout != null) {
      throw new IllegalArgumentException("MainLayout may only be set once!");
    }
    this.mainLayout = mainLayout;
  }

  @Override
  protected String getViewNameAfterClose() {
    // TODO implement
    return "";
  }

}
