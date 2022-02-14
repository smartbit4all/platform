package org.smartbit4all.ui.vaadin.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.Session;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;
import org.springframework.util.ObjectUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.shared.Registration;

// TODO inherit from ui-common / UINavigationImpl?
public class UINavigationVaadinTabbed extends UINavigationVaadinCommon {

  private static final Logger log = LoggerFactory.getLogger(UINavigationVaadinTabbed.class);

  private AppLayout mainView;
  private Tabs tabs;

  private Div tabContents;

  private Map<Tab, NavigationTarget> navigationTargetsByTab;

  private Map<Tab, Component> viewsByTab;

  private Map<String, Tab> tabsByViewObjectId;
  private Map<UUID, Tab> tabsByUUID;

  private List<Consumer<Component>> onTabChangeListeners = new ArrayList<>();

  private boolean hideDrawerOnSelect = false;

  public UINavigationVaadinTabbed(UI ui, UserSessionApi userSessionApi) {
    super(ui, userSessionApi);
    navigationTargetsByTab = new HashMap<>();
    viewsByTab = new HashMap<>();
    tabsByViewObjectId = new HashMap<>();
    tabsByUUID = new HashMap<>();
    tabs = new Tabs(true);
    tabs.setSizeFull();
    tabContents = new Div();
    tabContents.setSizeFull();
    tabs.addSelectedChangeListener(event -> {
      viewsByTab.values().forEach(view -> view.setVisible(false));
      Tab selectedTab = tabs.getSelectedTab();
      if (selectedTab != null) {
        Component selectedView = viewsByTab.get(selectedTab);
        if (selectedView != null) {
          selectedView.setVisible(true);
          onTabChangeListeners.forEach(l -> l.accept(selectedView));
        }
      }
    });
    if (userSessionApi.currentSession() != null) {
      userSessionApi.currentSession().subscribeForParameterChange(UINAVIGATION_CURRENT_NAV_TARGET,
          this::sessionParameterChange);
    }
  }

  private void sessionParameterChange(String paramKey) {
    if (UINAVIGATION_CURRENT_NAV_TARGET.equals(paramKey)) {
      Session session = userSessionApi.currentSession();
      navigateToInternal((NavigationTarget) session.getParameter(UINAVIGATION_CURRENT_NAV_TARGET));
    }
  }

  public Registration addTabChangedListener(Consumer<Component> onTabChangeListener) {
    return Registration.addAndRemove(onTabChangeListeners, onTabChangeListener);
  }

  public void setMainView(AppLayout mainView) {
    if (this.mainView != null) {
      throw new IllegalArgumentException("MainView may only be set once!");
    }
    this.mainView = mainView;
    this.mainView.addToNavbar(true, tabs);
    this.mainView.setContent(tabContents);
  }

  @Override
  protected void navigateToInternal(NavigationTarget navigationTarget) {
    ui.access(() -> {
      try {

        Component view;
        if (navigationTarget.getType() == NavigationTargetType.DIALOG) {
          view = navigateToDialog(navigationTarget);
        } else {
          view = navigateToTab(navigationTarget);
        }

        callHasUrlImplementation(navigationTarget, view);
      } catch (Exception e) {
        log.error("Unexpected error", e);
      }
    });
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
      view.setVisible(false);
      tab = createTab(navigationTarget);
      tabContents.add(view);
      navigationTargetsByTab.put(tab, navigationTarget);
      viewsByTab.put(tab, view);
      tabs.add(tab);
      if (viewObjectId != null) {
        tabsByViewObjectId.put(viewObjectId, tab);
      }
    }
    tabsByUUID.put(navigationTarget.getUuid(), tab);
    tabs.setSelectedTab(tab);
    if (mainView != null) {
      if (hideDrawerOnSelect && mainView.isDrawerOpened()) {
        mainView.setDrawerOpened(false);
      }
    }

    if (view instanceof TabAwareComponent) {
      ((TabAwareComponent) view).setTab(tab);
    }

    return view;
  }

  private Tab createTab(NavigationTarget navigationTarget) {
    String title = calculateTitle(navigationTarget);
    String iconKey = calculateIcon(navigationTarget);
    Icon icon = ObjectUtils.isEmpty(iconKey) ? null : new Icon(iconKey);

    Tab tab = createTab(title, icon);

    return tab;
  }

  protected Tab createTab(String title, Icon icon) {
    Tab tab = new Tab(title);

    if (icon != null) {
      tab.add(icon);
    }

    Button closeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), event -> closeTab(tab));
    closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
    tab.add(closeButton);
    return tab;
  }

  private void checkNavigationParameters(NavigationTarget navigationTarget) {
    if (VaadinService.getCurrent() == null) {
      throw new RuntimeException("VaadinService is not available!");
    }
    NavigableViewDescriptor viewDescriptor = getViewDescriptorByNavigationTarget(navigationTarget);
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
    callBeforeLeave(viewToClose);
    viewsByTab.remove(tab);
    NavigationTarget targetToClose = navigationTargetsByTab.get(tab);
    String viewObjectId = getViewObjectId(targetToClose);
    if (viewObjectId != null) {
      tabsByViewObjectId.remove(viewObjectId);
    }
    tabContents.remove(viewToClose);
    navigationTargetsByTab.remove(tab);
  }

  @Override
  protected String getViewNameAfterClose() {
    // this is called after tabs.remove(tab) --> this will be correct
    Tab tabSelectedAfterClose = tabs.getSelectedTab();
    String viewNameSelectedAfterClose;
    if (tabSelectedAfterClose != null) {
      viewNameSelectedAfterClose =
          navigationTargetsByTab.get(tabSelectedAfterClose).getViewName();
    } else {
      // TODO default location?
      viewNameSelectedAfterClose = "";
    }
    return viewNameSelectedAfterClose;
  }

  public boolean isHideDrawerOnSelect() {
    return hideDrawerOnSelect;
  }

  public void setHideDrawerOnSelect(boolean hideDrawerOnSelect) {
    this.hideDrawerOnSelect = hideDrawerOnSelect;
  }

  @Override
  public void close(UUID navigationTargetUuid) {
    ui.access(() -> {
      if (dialogsByUUID.containsKey(navigationTargetUuid)) {
        closeDialog(navigationTargetUuid);
      } else {
        NavigationTarget target = navigationTargetsByUUID.get(navigationTargetUuid);
        Tab tab = tabsByViewObjectId.get(getViewObjectId(target));
        closeTab(tab);
      }
      super.close(navigationTargetUuid);
    });
  }

  @Override
  public void setTitle(UUID navigationTargetUuid, String title) {
    Tab tab = tabsByUUID.get(navigationTargetUuid);
    if (tab != null) {
      tab.getElement().getChild(0).setText(title);
    } else {
      super.setTitle(navigationTargetUuid, title);
    }
  }

  public Tabs getTabs() {
    return tabs;
  }

  public Div getTabContents() {
    return tabContents;
  }

  public static interface TabAwareComponent {
    void setTab(Tab tab);
  }
}
