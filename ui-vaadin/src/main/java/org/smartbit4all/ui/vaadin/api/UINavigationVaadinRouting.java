package org.smartbit4all.ui.vaadin.api;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.router.RouterLayout;

public class UINavigationVaadinRouting extends UINavigationVaadinCommon {


  private static final Logger log = LoggerFactory.getLogger(UINavigationVaadinRouting.class);

  private Class<? extends RouterLayout> mainLayout;

  public UINavigationVaadinRouting(UI ui, UserSessionApi userSessionApi) {
    super(ui, userSessionApi);
    initSessionParameterListener();
  }

  @Override
  protected void navigateToInternal(NavigationTarget navigationTarget) {
    UUID apiUUID = (UUID) navigationTarget.getParameters().get(UINAVIGATION_UUID);
    if (uuid.equals(apiUUID)) {
      ui.access(() -> {
        NavigationTarget oldTarget = ObjectEditing.currentNavigationTarget.get();
        try {
          ObjectEditing.currentNavigationTarget.set(navigationTarget);
          try {
            if (navigationTarget.getType() == NavigationTargetType.DIALOG) {
              Component view = navigateToDialog(navigationTarget);
              callHasUrlImplementation(navigationTarget, view);
            } else {
              ui.navigate(navigationTarget.getViewName(), createQueryParameters(navigationTarget));
            }
          } catch (Exception e) {
            log.error("Unexpected error", e);
          }
        } finally {
          ObjectEditing.currentNavigationTarget.set(oldTarget);
        }
      });
    }
  }

  @Override
  protected Class<? extends Component> getViewClassByNavigationTarget(
      NavigationTarget navigationTarget) {
    Class<? extends Component> clazz = super.getViewClassByNavigationTarget(navigationTarget);
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
      String template = route.getTemplate();
      if (template != null && template.contains("/:")) {
        template = template.split("/:")[0];
      }
      if (viewName.equals(template)) {
        viewClass = route.getNavigationTarget();
        break;
      }
    }
    if (viewClass == null) {
      throw new IllegalArgumentException("No view found for dialog " + viewName);
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
    Class<? extends Component> viewClass = navigableViewClasses.get(viewName);
    registerVaadinRouteIfNotExists(viewName, viewClass);
  }

  @Override
  public void registerView(NavigableViewDescriptor viewDescriptor, NavigationTargetType type) {
    super.registerView(viewDescriptor, type);
    if (type == NavigationTargetType.NORMAL) {
      String viewName = viewDescriptor.getViewName();
      Class<? extends Component> viewClass = navigableViewClassesByType.get(type).get(viewName);
      registerVaadinRouteIfNotExists(viewName, viewClass);
    }
  }

  @SuppressWarnings("unchecked")
  private void registerVaadinRouteIfNotExists(String viewName,
      Class<? extends Component> viewClass) {
    RouteConfiguration configuration = RouteConfiguration.forSessionScope();
    if (!configuration.isPathAvailable(viewName)) {
      if (mainLayout != null) {
        configuration.setRoute(viewName, viewClass, mainLayout);
      } else {
        configuration.setRoute(viewName, viewClass);
      }
    }
  }

  @Override
  public void close(UUID navigationTargetUuid) {
    if (dialogsByUUID.containsKey(navigationTargetUuid)) {
      closeDialog(navigationTargetUuid);
    } else {
      // TODO what does close mean in this case? navigate back?
    }
    super.close(navigationTargetUuid);
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
