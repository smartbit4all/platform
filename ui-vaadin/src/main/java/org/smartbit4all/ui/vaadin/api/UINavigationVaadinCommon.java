package org.smartbit4all.ui.vaadin.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.ui.api.navigation.model.Message;
import org.smartbit4all.ui.api.navigation.model.MessageResult;
import org.smartbit4all.ui.api.navigation.model.MessageResultType;
import org.smartbit4all.ui.api.navigation.model.MessageType;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;
import org.smartbit4all.ui.common.api.UINavigationApiCommon;
import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog.OpenedChangeEvent;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.VaadinService;

public abstract class UINavigationVaadinCommon extends UINavigationApiCommon {

  protected Map<String, Class<? extends Component>> navigableViewClasses;
  protected Map<NavigationTargetType, Map<String, Class<? extends Component>>> navigableViewClassesByType;

  protected UI ui;
  protected Map<UUID, Dialog> dialogsByUUID;
  protected Map<UUID, Label> dialogLabelsByUUID;
  protected Map<UUID, Component> dialogViewsByUUID;

  private static final String PARAM = "param";

  public UINavigationVaadinCommon(UI ui, UserSessionApi userSessionApi) {
    super(userSessionApi);
    this.ui = ui;
    ui.addDetachListener(event -> handleUIDestroyed());
    navigableViewClasses = new HashMap<>();
    navigableViewClassesByType = new HashMap<>();
    dialogsByUUID = new HashMap<>();
    dialogViewsByUUID = new HashMap<>();
    dialogLabelsByUUID = new HashMap<>();
  }

  @Override
  public void registerView(NavigableViewDescriptor viewDescriptor) {
    super.registerView(viewDescriptor);
    try {
      @SuppressWarnings("unchecked")
      Class<? extends Component> viewClass = getViewClass(viewDescriptor.getViewClassName());
      navigableViewClasses.put(viewDescriptor.getViewName(), viewClass);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(
          "View class could be loaded for view " + viewDescriptor.getViewName() + "!", e);
    }
  }

  @Override
  public void registerView(NavigableViewDescriptor viewDescriptor, NavigationTargetType type) {
    super.registerView(viewDescriptor, type);
    try {
      @SuppressWarnings("unchecked")
      Class<? extends Component> viewClass = getViewClass(viewDescriptor.getViewClassName());
      Map<String, Class<? extends Component>> viewClasses = navigableViewClassesByType.get(type);
      if (viewClasses == null) {
        viewClasses = new HashMap<>();
        navigableViewClassesByType.put(type, viewClasses);
      }
      viewClasses.put(viewDescriptor.getViewName(), viewClass);
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
    // this might be unnecessary, since createView is called from navigateToInternal only
    NavigationTarget oldTarget = ObjectEditing.currentNavigationTarget.get();
    try {
      ObjectEditing.currentNavigationTarget.set(navigationTarget);
      return VaadinService.getCurrent()
          .getInstantiator()
          .createComponent(getViewClassByNavigationTarget(navigationTarget));
    } finally {
      ObjectEditing.currentNavigationTarget.set(oldTarget);
    }
  }

  protected Class<? extends Component> getViewClassByNavigationTarget(
      NavigationTarget navigationTarget) {
    if (navigationTarget.getType() != null) {
      Map<String, Class<? extends Component>> viewClasses =
          navigableViewClassesByType.get(navigationTarget.getType());
      if (viewClasses != null) {
        Class<? extends Component> viewClass = viewClasses.get(navigationTarget.getViewName());
        if (viewClass != null) {
          return viewClass;
        }
      }
    }
    return navigableViewClasses.get(navigationTarget.getViewName());
  }

  /**
   * Shows navigationTarget in dialog. Doesn't call callHasUrlImplementation()!
   * 
   * @param navigationTarget
   * @return
   */
  protected Component navigateToDialog(NavigationTarget navigationTarget) {
    UUID dialogUUID = navigationTarget.getUuid();
    Dialog dialog = dialogsByUUID.get(dialogUUID);
    Component view = dialogViewsByUUID.get(dialogUUID);
    if (view == null) {
      view = createView(navigationTarget);
      dialogViewsByUUID.put(dialogUUID, view);
    }
    if (dialog == null) {
      dialog = createDialog(navigationTarget, view, dialogUUID);
      dialogsByUUID.put(dialogUUID, dialog);
    }
    dialog.setModal(true);
    dialog.setCloseOnOutsideClick(false);
    dialog.setCloseOnEsc(false);
    if (navigationTarget.getFullSize()) {
      dialog.setSizeFull();
    }
    dialog.open();
    return view;
  }

  protected Dialog createDialog(NavigationTarget navigationTarget, Component view,
      UUID dialogUUID) {
    Dialog dialog = new Dialog();
    Icon titleIcon = null;
    String iconCode = calculateIcon(navigationTarget);
    if (!Strings.isNullOrEmpty(iconCode)) {
      titleIcon = new Icon(iconCode);
      titleIcon.addClassName("sb4-dialog1-title-icon");
    }

    String titleText = calculateTitle(navigationTarget);
    Label title = new Label(titleText);
    // TODO create DialogHeader composite, store it and use it for setTitle, setIcon?
    dialogLabelsByUUID.put(dialogUUID, title);

    Icon closeIcon = new Icon(VaadinIcon.CLOSE_SMALL);
    closeIcon.addClassName("sb4-dialog1-close-icon");
    closeIcon.addClickListener(e -> dialog.close());

    FlexLayout header;
    if (titleIcon == null) {
      header = new FlexLayout(title, closeIcon);
    } else {
      header = new FlexLayout(titleIcon, title, closeIcon);
    }
    header.addClassName("sb4-dialog1-header");

    FlexLayout dialogLayout = new FlexLayout(header, view);
    dialogLayout.addClassName("sb4-dialog1");
    if (Boolean.TRUE.equals(navigationTarget.getFullSize())) {
      dialogLayout.setHeightFull();
    }
    dialog.add(dialogLayout);

    dialog.addOpenedChangeListener(event -> onDialogClose(dialogUUID, event));

    return dialog;
  }

  protected void closeDialog(UUID navigationTargetUuid) {
    Dialog dialog = dialogsByUUID.get(navigationTargetUuid);
    if (dialog != null) {
      dialog.close();
    }
    // all other logic is handled in onDialogClose closeListener
  }

  protected void onDialogClose(Object dialogUUID, OpenedChangeEvent<Dialog> event) {
    if (!event.getSource().isOpened()) {
      Component viewToClose = dialogViewsByUUID.get(dialogUUID);
      if (viewToClose != null) {
        callBeforeLeave(viewToClose);
        dialogViewsByUUID.remove(dialogUUID);
      }
      dialogsByUUID.remove(dialogUUID);
      dialogLabelsByUUID.remove(dialogUUID);
    }
  }

  protected void callHasUrlImplementation(NavigationTarget navigationTarget, Component view) {
    if (view instanceof HasUrlParameter) {
      QueryParameters queryParams = createQueryParameters(navigationTarget);
      Location location = new Location(navigationTarget.getViewName(), queryParams);
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

  @Override
  public void showMessage(Message message, Consumer<MessageResult> messageListener) {
    // TODO icon handling, components for header / text
    ConfirmDialog dialog = new ConfirmDialog();
    dialog.setHeader(message.getHeader());
    dialog.setText(message.getText());

    MessageResult confirmed = findMessageResult(message, MessageResultType.CONFIRM);
    MessageResult rejected = findMessageResult(message, MessageResultType.REJECT);
    MessageResult canceled = findMessageResult(message, MessageResultType.CANCEL);

    if (confirmed != null) {
      dialog.setConfirmText(confirmed.getLabel());
      if (messageListener != null) {
        dialog.addConfirmListener(e -> messageListener.accept(confirmed));
      }
    } else {
      // TODO default text
      dialog.setConfirmText("OK");
    }

    // TODO other types
    if (message.getType() == MessageType.ERROR) {
      dialog.setConfirmButtonTheme("error primary");
    }

    if (rejected != null) {
      dialog.setRejectable(true);
      dialog.setRejectText(rejected.getLabel());
      if (messageListener != null) {
        dialog.addRejectListener(e -> messageListener.accept(rejected));
      }
    }
    if (canceled != null) {
      dialog.setCancelable(true);
      dialog.setCancelText(canceled.getLabel());
      if (messageListener != null) {
        dialog.addCancelListener(e -> messageListener.accept(canceled));
      }
    }
    ui.access(dialog::open);
  }

  private MessageResult findMessageResult(Message message, MessageResultType type) {
    return message.getPossibleResults().stream()
        .filter(r -> r.getType() == type)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void setTitle(UUID navigationTargetUuid, String title) {
    if (dialogLabelsByUUID.containsKey(navigationTargetUuid)) {
      Label label = dialogLabelsByUUID.get(navigationTargetUuid);
      if (label != null) {
        label.setText(title);
      }
    }

  }

  protected QueryParameters createQueryParameters(NavigationTarget navigationTarget) {
    if (navigationTarget == null || navigationTarget.getUuid() == null) {
      return null;
    }
    Map<String, List<String>> params = new HashMap<>();
    params.put(PARAM, Arrays.asList(navigationTarget.getUuid().toString()));
    return new QueryParameters(params);
  }

}
