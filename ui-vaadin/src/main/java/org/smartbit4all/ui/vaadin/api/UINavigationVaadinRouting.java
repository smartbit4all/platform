package org.smartbit4all.ui.vaadin.api;

import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.vaadin.components.navigation.Navigation;
import com.vaadin.flow.component.UI;

// TODO inherit from ui-common / UINavigationImpl?
public class UINavigationVaadinRouting implements UINavigationApi {

  private UserSessionApi userSessionApi;
  private UI ui;

  public UINavigationVaadinRouting(UI ui, UserSessionApi userSessionApi) {
    this.ui = ui;
    this.userSessionApi = userSessionApi;
  }

  @Override
  public void navigateTo(NavigationTarget navigationTarget) {

    Navigation navigation = Navigation.to(navigationTarget.getViewName());

    if (navigationTarget.getParameters() != null) {
      navigationTarget.getParameters().entrySet().stream()
          .forEach(entry -> navigation.param(entry.getKey(), entry.getValue()));
    }
    navigation.navigate(ui);

  }

}
