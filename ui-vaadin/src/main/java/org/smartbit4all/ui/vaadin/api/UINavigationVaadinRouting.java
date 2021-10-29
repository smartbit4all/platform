package org.smartbit4all.ui.vaadin.api;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;
import org.smartbit4all.ui.vaadin.components.navigation.UIViewParameterVaadinTransition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.router.RouterLayout;

// TODO inherit from ui-common / UINavigationImpl?
public class UINavigationVaadinRouting extends UINavigationVaadinCommon {


  private static final Logger log = LoggerFactory.getLogger(UINavigationVaadinRouting.class);

  private Class<? extends RouterLayout> mainLayout;

  public UINavigationVaadinRouting(UI ui, UserSessionApi userSessionApi) {
    super(ui, userSessionApi);
  }

  @Override
  public void navigateTo(NavigationTarget navigationTarget) {
    super.navigateTo(navigationTarget);
    try {
      ObjectEditing.currentConstructionUUID.set(navigationTarget.getUuid());
      String viewName = navigationTarget.getViewName();
      try (UIViewParameterVaadinTransition param =
          new UIViewParameterVaadinTransition(navigationTarget.getParameters())) {
        if (navigationTarget.getType() == NavigationTargetType.DIALOG) {
          Component view = navigateToDialog(navigationTarget);
          callHasUrlImplementation(viewName, param, view);
        } else {
          ui.navigate(viewName, param.construct());
        }
      } catch (Exception e) {
        log.error("Unexpected error", e);
      }
    } finally {
      ObjectEditing.currentConstructionUUID.set(null);
    }

  }

  @Override
  protected Class<? extends Component> getViewClassByName(NavigationTarget navigationTarget) {
    // TODO Auto-generated method stub
    Class<? extends Component> clazz = super.getViewClassByName(navigationTarget);
    if (clazz == null) {
      return registerViewByRouting(navigationTarget.getViewName());
    }
    return clazz;
  }

  /**
   * Applications using Vaadin routing typically doesn't call registerView, they register views in
   * Vaadin routing configuration instead. When displaying dialogs, we need this to register views
   * found in routing.
   * 
   * @param viewName
   * @return
   */
  private Class<? extends Component> registerViewByRouting(String viewName) {
    List<RouteData> routes = RouteConfiguration.forSessionScope().getAvailableRoutes();

    Class<? extends Component> viewClass = null;
    for (RouteData route : routes) {
      if (viewName.equals(route.getUrl())) {
        viewClass = route.getNavigationTarget();
        break;
      }
    }
    if (viewClass == null) {
      throw new RuntimeException("No view found for dialog " + viewName);
    }
    registerView(new NavigableViewDescriptor()
        .viewName(viewName)
        .viewClassName(viewClass.getName()));
    return viewClass;
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
