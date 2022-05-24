package org.smartbit4all.ui.common.api;

import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.ui.api.navigation.model.Message;
import org.smartbit4all.ui.api.navigation.model.MessageResult;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.viewmodel.ViewModel;

public class UINavigationApiHeadless extends UINavigationApiCommon {

  private static final Logger log = LoggerFactory.getLogger(UINavigationApiHeadless.class);

  static final ThreadLocal<NavigationTarget> uiToOpen = new ThreadLocal<>();

  static final ThreadLocal<Message> messageToOpen = new ThreadLocal<>();
  static final ThreadLocal<Consumer<MessageResult>> messageToOpenHandler = new ThreadLocal<>();

  public UINavigationApiHeadless(UserSessionApi userSessionApi) {
    super(userSessionApi);
  }

  @Override
  protected void navigateToInternal(NavigationTarget navigationTarget) {
    if ("logout".equals(navigationTarget.getViewName())) {
      // TODO ???
      clearAndRemoveSession();

      UINavigationApiHeadless.uiToOpen.set(navigationTarget);
      return;
    }
    NavigableViewDescriptor desc = getViewDescriptorByNavigationTarget(navigationTarget);
    if (desc == null) {
      throw new IllegalArgumentException(
          "ViewClass not found for view" + navigationTarget.getViewName());
    }
    try {
      Class<?> viewModelClass = Class.forName(desc.getViewClassName());
      ViewModel viewModel = getViewModelByUuidInternal(navigationTarget.getUuid());
      if (viewModel != null) {
        viewModel = ReflectionUtility.getProxyTarget(viewModel);
        if (!viewModelClass.isAssignableFrom(viewModel.getClass())) {
          throw new IllegalStateException(
              "Existing view's class (" + viewModel.getClass().getName()
                  + ") is not assignable from specified viewModelClass (" + viewModelClass.getName()
                  + ") for view " + navigationTarget.getViewName());
        }

      } else {
        if (!ViewModel.class.isAssignableFrom(viewModelClass)) {
          throw new IllegalArgumentException(
              "ViewClass is not ViewModel for view" + navigationTarget.getViewName());
        }
        viewModel =
            createAndInitViewModel(navigationTarget, (Class<? extends ViewModel>) viewModelClass);
        putViewModelByUuidInternal(navigationTarget.getUuid(), viewModel);
      }
      UINavigationApiHeadless.uiToOpen.set(navigationTarget);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(
          "ViewClass not found for view" + navigationTarget.getViewName());
    }
  }

  @Override
  public void showMessage(Message message, Consumer<MessageResult> messageListener) {
    super.showMessage(message, messageListener);
    messageToOpen.set(message);
    messageToOpenHandler.set(messageListener);
  }


  public static NavigationTarget getUiToOpen() {
    return uiToOpen.get();
  }

  public static void clearUiToOpen() {
    uiToOpen.remove();
  }

  public static Message getMessageToOpen() {
    return messageToOpen.get();
  }

  public static void clearMessageToOpen() {
    messageToOpen.remove();
  }

  public static Consumer<MessageResult> getMessageToOpenHandler() {
    return messageToOpenHandler.get();
  }

  public static void clearMessageToOpenHandler() {
    messageToOpenHandler.remove();
  }

  @Override
  public <T extends ViewModel> T createAndAddChildViewModel(ViewModel parent, String path,
      Class<T> clazz, NavigationTarget navigationTarget) {
    T viewModel = super.createAndAddChildViewModel(parent, path, clazz, navigationTarget);
    putViewModelByUuidInternal(viewModel.getUuid(), viewModel);
    return viewModel;
  }

  @Override
  public void close(UUID navigationTargetUuid) {
    ViewModel viewModel = getViewModelByUuidInternal(navigationTargetUuid);
    if (viewModel != null) {
      viewModel.onCloseWindow();
    }
    super.close(navigationTargetUuid);
  }
}
