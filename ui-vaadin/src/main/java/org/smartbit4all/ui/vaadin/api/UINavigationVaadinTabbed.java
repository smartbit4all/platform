package org.smartbit4all.ui.vaadin.api;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;
import org.smartbit4all.ui.vaadin.components.navigation.UIViewParameterVaadinTransition;
import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.VaadinService;

// TODO inherit from ui-common / UINavigationImpl?
public class UINavigationVaadinTabbed extends UINavigationVaadinCommon {

  private static final Logger log = LoggerFactory.getLogger(UINavigationVaadinTabbed.class);

  private UserSessionApi userSessionApi;
  private AppLayout mainView;
  private UI ui;
  private Tabs tabs;

  private Div tabContents;

  private Map<Tab, NavigationTarget> navigationTargetsByTab;

  private Map<Tab, Component> viewsByTab;

  private Map<String, Tab> tabsByViewObjectId;

  private boolean hideDrawerOnSelect = false;

  public UINavigationVaadinTabbed(UI ui, UserSessionApi userSessionApi) {
    this.ui = ui;
    this.userSessionApi = userSessionApi;
    navigationTargetsByTab = new HashMap<>();
    viewsByTab = new HashMap<>();
    tabsByViewObjectId = new HashMap<>();
  }

  public void setMainView(AppLayout mainView) {
    if (this.mainView != null) {
      throw new IllegalArgumentException("MainView may only be set once!");
    }
    this.mainView = mainView;
    FlexLayout content = new FlexLayout();
    content.setFlexDirection(FlexDirection.COLUMN);
    tabs = new Tabs(true);
    tabs.setSizeFull();
    this.mainView.addToNavbar(true, tabs);
    tabContents = new Div();
    tabContents.setSizeFull();
    content.add(tabContents);
    mainView.setContent(content);
    tabs.addSelectedChangeListener(event -> {
      viewsByTab.values().forEach(view -> view.setVisible(false));
      Tab selectedTab = tabs.getSelectedTab();
      if (selectedTab != null) {
        Component selectedView = viewsByTab.get(selectedTab);
        if (selectedView != null) {
          selectedView.setVisible(true);
        }
      }
    });
  }

  @Override
  public void navigateTo(NavigationTarget navigationTarget) {
    checkNavigationParameters(navigationTarget);
    super.navigateTo(navigationTarget);
    try (UIViewParameterVaadinTransition param =
        new UIViewParameterVaadinTransition(navigationTarget.getParameters())) {

      Component view;
      if (navigationTarget.getType() == NavigationTargetType.DIALOG) {
        view = navigateToDialog(navigationTarget);
      } else {
        view = navigateToTab(navigationTarget);
      }

      callHasUrlImplementation(navigationTarget.getViewName(), param, view);
    } catch (Exception e) {
      log.error("Unexpected error", e);
    }

  }

  private Component navigateToDialog(NavigationTarget navigationTarget) {
    throw new UnsupportedOperationException("navigateToDialog not implemented yet!");
  }

  private Component navigateToTab(NavigationTarget navigationTarget) {
    String viewObjectId = getViewObjectId(navigationTarget);
    Component view;
    Tab tab;
    if (viewObjectId != null && tabsByViewObjectId.containsKey(viewObjectId)) {
      // TODO add navigationTarget.getUuid() to already existing tab's uuid list?
      tab = tabsByViewObjectId.get(viewObjectId);
      view = viewsByTab.get(tab);
    } else {
      view = createView(navigationTarget);
      tab = createTab(navigationTarget);
      tabContents.add(view);
      navigationTargetsByTab.put(tab, navigationTarget);
      viewsByTab.put(tab, view);
      tabs.add(tab);
      if (viewObjectId != null) {
        tabsByViewObjectId.put(viewObjectId, tab);
      }
    }
    closeCurrentTabBeforeNavigation(navigationTarget, tab);
    tabs.setSelectedTab(tab);
    if (hideDrawerOnSelect && mainView.isDrawerOpened()) {
      mainView.setDrawerOpened(false);
    }
    return view;
  }

  private void closeCurrentTabBeforeNavigation(NavigationTarget navigationTarget, Tab tab) {
    if (navigationTarget.getCloseAfterNavigation() != null) {
      if (navigationTarget.getCloseAfterNavigation()) {
        Tab currentSelectedTab = tabs.getSelectedTab();
        if (currentSelectedTab != null && currentSelectedTab != tab) {
          closeTab(currentSelectedTab);
        }
      }
    }
  }

  private void callHasUrlImplementation(String viewName, UIViewParameterVaadinTransition param,
      Component view) {
    if (view instanceof HasUrlParameter) {
      QueryParameters queryParams = param.construct();
      Location location = new Location(viewName, queryParams);
      BeforeEnterEvent event =
          new BeforeEnterEvent(ui.getRouter(), null, location, null, ui, Collections.emptyList());
      ((HasUrlParameter<?>) view).setParameter(event, null);
    }
  }

  private Tab createTab(NavigationTarget navigationTarget) {
    String viewName = navigationTarget.getViewName();
    NavigableViewDescriptor viewDescriptor = navigableViews.get(viewName);
    // title
    String title = viewDescriptor.getTitle();
    if (Strings.isNullOrEmpty(title)) {
      title = viewName;
    }
    Tab tab = new Tab(title);
    // icon
    String icon = viewDescriptor.getIcon();
    if (!Strings.isNullOrEmpty(icon)) {
      tab.add(new Icon(icon));
    }
    // closeButton
    Button closeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), event -> closeTab(tab));
    closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
    tab.add(closeButton);

    return tab;
  }

  private Component createView(NavigationTarget navigationTarget) {
    try {
      ObjectEditing.currentConstructionUUID.set(navigationTarget.getUuid());
      Component view = VaadinService.getCurrent().getInstantiator()
          .createComponent(navigableViewClasses.get(navigationTarget.getViewName()));
      view.setVisible(false);
      return view;
    } finally {
      ObjectEditing.currentConstructionUUID.set(null);
    }
  }

  private void checkNavigationParameters(NavigationTarget navigationTarget) {
    if (mainView == null) {
      throw new IllegalArgumentException("MainView must be set before navigation!");
    }
    if (VaadinService.getCurrent() == null) {
      throw new RuntimeException("VaadinService is not available!");
    }
    NavigableViewDescriptor viewDescriptor = navigableViews.get(navigationTarget.getViewName());
    if (viewDescriptor == null) {
      throw new RuntimeException(
          "viewDescriptor not found for view " + navigationTarget.getViewName() + "!");
    }

  }

  private String getViewObjectId(NavigationTarget navigationTarget) {
    URI viewObjectUri = navigationTarget.getViewObjectUri();
    if (viewObjectUri == null) {
      if (navigationTarget.getParameters().containsKey("entry")) {
        Object viewObjectUriObj = navigationTarget.getParameters().get("entry");
        if (viewObjectUriObj instanceof URI) {
          viewObjectUri = (URI) viewObjectUriObj;
        }
      }
    }
    String id =
        viewObjectUri == null ? navigationTarget.getUuid().toString() : viewObjectUri.toString();
    return navigationTarget.getViewName() + "-" + id;
  }


  private void closeTab(Tab tab) {
    tabs.remove(tab);
    Component viewToClose = viewsByTab.get(tab);
    if (viewToClose instanceof BeforeLeaveObserver) {
      Tab tabSelectedAfterClose = tabs.getSelectedTab();
      String viewNameSelectedAfterClose;
      if (tabSelectedAfterClose != null) {
        viewNameSelectedAfterClose =
            navigationTargetsByTab.get(tabSelectedAfterClose).getViewName();
      } else {
        // TODO default location?
        viewNameSelectedAfterClose = "";
      }
      Location location = new Location(viewNameSelectedAfterClose);
      BeforeLeaveEvent event =
          new BeforeLeaveEvent(ui.getRouter(), null, location, null, ui, Collections.emptyList());
      ((BeforeLeaveObserver) viewToClose).beforeLeave(event);
    }
    viewsByTab.remove(tab);
    NavigationTarget targetToClose = navigationTargetsByTab.get(tab);
    String viewObjectId = getViewObjectId(targetToClose);
    if (viewObjectId != null) {
      tabsByViewObjectId.remove(viewObjectId);
    }
    tabContents.remove(viewToClose);
    navigationTargetsByTab.remove(tab);
  }

  public boolean isHideDrawerOnSelect() {
    return hideDrawerOnSelect;
  }

  public void setHideDrawerOnSelect(boolean hideDrawerOnSelect) {
    this.hideDrawerOnSelect = hideDrawerOnSelect;
  }

  @Override
  public void close(UUID navigationTargetUuid) {
    // TODO test more..
    NavigationTarget target = navigationTargetsByUUID.get(navigationTargetUuid);
    Tab tab = tabsByViewObjectId.get(getViewObjectId(target));
    closeTab(tab);
  }

}
