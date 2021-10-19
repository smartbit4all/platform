package org.smartbit4all.ui.vaadin.api;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
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
public class UINavigationVaadinTabbed implements UINavigationApi {


  private static final Logger log = LoggerFactory.getLogger(UINavigationVaadinTabbed.class);

  private UserSessionApi userSessionApi;
  private AppLayout mainView;
  private UI ui;
  private Tabs tabs;

  private Div tabContents;

  private Map<Tab, Component> viewsByTab;
  private Map<Tab, String> viewNamesByTab;

  private Map<String, NavigableViewDescriptor> navigableViews;

  private Map<String, Map<URI, Tab>> tabsByViewAndUri;

  private boolean hideDrawerOnSelect = false;

  public UINavigationVaadinTabbed(UI ui, UserSessionApi userSessionApi) {
    this.ui = ui;
    this.userSessionApi = userSessionApi;
    viewsByTab = new HashMap<>();
    viewNamesByTab = new HashMap<>();
    navigableViews = new HashMap<>();
    tabsByViewAndUri = new HashMap<>();
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
      viewsByTab.values().forEach(page -> page.setVisible(false));
      Component selectedPage = viewsByTab.get(tabs.getSelectedTab());
      selectedPage.setVisible(true);
    });
  }

  @Override
  public void navigateTo(NavigationTarget navigationTarget) {
    if (mainView == null) {
      throw new IllegalArgumentException("MainView must be set before navigation!");
    }
    VaadinService vaadinService = VaadinService.getCurrent();
    if (vaadinService == null) {
      throw new RuntimeException("VaadinService is not available!");
    }

    String viewName = navigationTarget.getViewName();
    try (UIViewParameterVaadinTransition param =
        new UIViewParameterVaadinTransition(navigationTarget.getParameters())) {

      URI viewEntityUri = null;
      Map<URI, Tab> tabsByUri = null;
      if (navigationTarget.getParameters().containsKey("entry")) {
        Object uriObject = navigationTarget.getParameters().get("entry");
        if (uriObject instanceof URI) {
          viewEntityUri = (URI) uriObject;
          tabsByUri = tabsByViewAndUri.get(viewName);
          if (tabsByUri == null) {
            tabsByUri = new HashMap<>();
            tabsByViewAndUri.put(viewName, tabsByUri);
          }
        }
      }
      NavigableViewDescriptor viewDescriptor = navigableViews.get(viewName);
      if (viewDescriptor == null) {
        throw new RuntimeException("viewDescriptor not found for view " + viewName + "!");
      }

      Component view;
      Tab tab;
      if (tabsByUri != null && viewEntityUri != null && tabsByUri.containsKey(viewEntityUri)) {
        tab = tabsByUri.get(viewEntityUri);
        view = viewsByTab.get(tab);
      } else {
        try {
          @SuppressWarnings("unchecked")
          Component viewCreated = vaadinService.getInstantiator()
              .createComponent(getViewClass(viewDescriptor.getViewClassName()));
          view = viewCreated;
        } catch (ClassNotFoundException e) {
          throw new RuntimeException("View class could be loaded for view " + viewName + "!", e);
        }
        String title = viewDescriptor.getTitle();
        if (Strings.isNullOrEmpty(title)) {
          title = viewName;
        }
        tab = new Tab(title);
        String icon = viewDescriptor.getIcon();
        if (!Strings.isNullOrEmpty(icon)) {
          tab.add(new Icon(icon));
        }
        Button closeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), event -> closeTab(tab));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        tab.add(closeButton);
        view.setVisible(false);
        tabContents.add(view);
        viewsByTab.put(tab, view);
        viewNamesByTab.put(tab, viewName);
        tabs.add(tab);
        if (tabsByUri != null && viewEntityUri != null) {
          tabsByUri.put(viewEntityUri, tab);
        }
      }
      if (view instanceof HasUrlParameter) {
        QueryParameters queryParams = param.construct();
        Location location = new Location(viewName, queryParams);
        BeforeEnterEvent event =
            new BeforeEnterEvent(ui.getRouter(), null, location, null, ui, Collections.emptyList());
        ((HasUrlParameter<?>) view).setParameter(event, null);
      }
      if (navigationTarget.getCloseAfterNavigation() != null) {
        if (navigationTarget.getCloseAfterNavigation()) {
          Tab currentSelectedTab = tabs.getSelectedTab();
          if (currentSelectedTab != null && currentSelectedTab != tab) {
            closeTab(currentSelectedTab);
          }
        }
      }
      tabs.setSelectedTab(tab);
      if (hideDrawerOnSelect && mainView.isDrawerOpened()) {
        mainView.setDrawerOpened(false);
      }
    } catch (Exception e) {
      log.error("Unexpected error", e);
    }

  }


  private void closeTab(Tab tab) {
    tabs.remove(tab);
    String viewName = viewNamesByTab.get(tab);
    Component viewToClose = viewsByTab.get(tab);
    if (viewToClose instanceof BeforeLeaveObserver) {
      Tab tabSelectedAfterClose = tabs.getSelectedTab();
      String viewNameSelectedAfterClose;
      if (tabSelectedAfterClose != null) {
        viewNameSelectedAfterClose = viewNamesByTab.get(tabSelectedAfterClose);
      } else {
        // TODO default location?
        viewNameSelectedAfterClose = "";
      }
      Location location = new Location(viewNameSelectedAfterClose);
      BeforeLeaveEvent event =
          new BeforeLeaveEvent(ui.getRouter(), null, location, null, ui, Collections.emptyList());
      ((BeforeLeaveObserver) viewToClose).beforeLeave(event);
    }
    tabContents.remove(viewToClose);
    viewsByTab.remove(tab);
    Map<URI, Tab> tabsByUri = tabsByViewAndUri.get(viewName);
    if (tabsByUri != null) {
      URI uri = null;
      for (Entry<URI, Tab> entry : tabsByUri.entrySet()) {
        if (tab == entry.getValue()) {
          uri = entry.getKey();
          break;
        }
      }
      if (uri != null) {
        tabsByUri.remove(uri);
      }
    }
  }


  @SuppressWarnings("rawtypes")
  private Class getViewClass(String viewClassName) throws ClassNotFoundException {
    return Class.forName(viewClassName);
  }

  public void registerView(NavigableViewDescriptor viewDescriptor) {
    navigableViews.put(viewDescriptor.getViewName(), viewDescriptor);
  }

  private String getViewClassByName(String viewName) {

    if (viewName.equals("kirendelesek")) {
      return "hu.idomsoft.novaugykoltseg.ui.vaadin.KirendelesekUI";
    }
    if (viewName.equals("kirendelesSzerkesztes")) {
      return "hu.idomsoft.novaugykoltseg.ui.vaadin.KirendelesSzerkesztesUI";
    }
    if (viewName.equals("dashboard")) {
      return "hu.idomsoft.novaugykoltseg.ui.vaadin.NovaUgykoltsegDashboardUI";
    }
    return null;
  }

  public boolean isHideDrawerOnSelect() {
    return hideDrawerOnSelect;
  }

  public void setHideDrawerOnSelect(boolean hideDrawerOnSelect) {
    this.hideDrawerOnSelect = hideDrawerOnSelect;
  }

}
