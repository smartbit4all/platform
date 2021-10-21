package org.smartbit4all.ui.vaadin.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.common.api.UINavigationApiCommon;
import org.smartbit4all.ui.vaadin.components.navigation.UIViewParameterVaadinTransition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.VaadinService;

public abstract class UINavigationVaadinCommon extends UINavigationApiCommon {

  protected Map<String, Class<? extends Component>> navigableViewClasses;

  protected UserSessionApi userSessionApi;
  protected UI ui;
  protected Map<UUID, Dialog> dialogsByUUID;
  protected Map<UUID, Component> dialogViewsByUUID;

  public UINavigationVaadinCommon(UI ui, UserSessionApi userSessionApi) {
    this.ui = ui;
    this.userSessionApi = userSessionApi;
    navigableViewClasses = new HashMap<>();
    dialogsByUUID = new HashMap<>();
    dialogViewsByUUID = new HashMap<>();
  }

  @Override
  public void registerView(NavigableViewDescriptor viewDescriptor) {
    super.registerView(viewDescriptor);
    try {
      @SuppressWarnings("rawtypes")
      Class<? extends Component> viewClass = getViewClass(viewDescriptor.getViewClassName());
      navigableViewClasses.put(viewDescriptor.getViewName(), viewClass);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(
          "View class could be loaded for view " + viewDescriptor.getViewName() + "!", e);
    }

  }

  @SuppressWarnings("rawtypes")
  private Class getViewClass(String viewClassName) throws ClassNotFoundException {
    return Class.forName(viewClassName);
  }

  protected Component createView(NavigationTarget navigationTarget) {
    try {
      ObjectEditing.currentConstructionUUID.set(navigationTarget.getUuid());
      Component view = VaadinService.getCurrent().getInstantiator()
          .createComponent(navigableViewClasses.get(navigationTarget.getViewName()));
      return view;
    } finally {
      ObjectEditing.currentConstructionUUID.set(null);
    }
  }

  protected Component navigateToDialog(NavigationTarget navigationTarget) {
    UUID dialogUUID = navigationTarget.getUuid();
    Dialog dialog = dialogsByUUID.get(dialogUUID);
    Component view = dialogViewsByUUID.get(dialogUUID);
    if (view == null) {
      view = createView(navigationTarget);
      dialogViewsByUUID.put(dialogUUID, view);
    }
    if (dialog == null) {
      dialog = new Dialog(view);
      dialog.addDialogCloseActionListener(event -> onDialogClose(dialogUUID));
      dialogsByUUID.put(dialogUUID, dialog);
    }
    dialog.open();
    return view;
  }

  protected void closeDialog(UUID navigationTargetUuid) {
    Dialog dialog = dialogsByUUID.get(navigationTargetUuid);
    if (dialog != null) {
      dialog.close();
    }
    // all other logic is handled in onDialogClose closeListener
  }

  protected void onDialogClose(Object dialogUUID) {
    Component viewToClose = dialogViewsByUUID.get(dialogUUID);
    if (viewToClose != null) {
      callBeforeLeave(viewToClose);
      dialogViewsByUUID.remove(dialogUUID);
    }
    dialogsByUUID.remove(dialogUUID);

  }

  protected void callHasUrlImplementation(String viewName, UIViewParameterVaadinTransition param,
      Component view) {
    if (view instanceof HasUrlParameter) {
      QueryParameters queryParams = param.construct();
      Location location = new Location(viewName, queryParams);
      BeforeEnterEvent event =
          new BeforeEnterEvent(ui.getRouter(), null, location, null, ui, Collections.emptyList());
      ((HasUrlParameter<?>) view).setParameter(event, null);
    }
  }

  protected void callBeforeLeave(Component viewToClose) {
    if (viewToClose instanceof BeforeLeaveObserver) {
      String viewNameSelectedAfterClose = getViewNameAfterClose();
      Location location = new Location(viewNameSelectedAfterClose);
      BeforeLeaveEvent event =
          new BeforeLeaveEvent(ui.getRouter(), null, location, null, ui, Collections.emptyList());
      ((BeforeLeaveObserver) viewToClose).beforeLeave(event);
    }
  }

  protected abstract String getViewNameAfterClose();

}
