package org.smartbit4all.ui.common.api;

import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.ui.api.navigation.model.Message;
import org.smartbit4all.ui.api.navigation.model.MessageResult;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;
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
    NavigableViewDescriptor desc = getViewDescriptorByNavigationTarget(navigationTarget);
    if (desc == null) {
      throw new IllegalArgumentException(
          "ViewClass not found for view" + navigationTarget.getViewName());
    }
    try {
      Class<?> viewModelClass = Class.forName(desc.getViewClassName());
      if (!ViewModel.class.isAssignableFrom(viewModelClass)) {
        throw new IllegalArgumentException(
            "ViewClass is not ViewModel for view" + navigationTarget.getViewName());
      }
      NavigationTarget oldTarget = ObjectEditing.currentNavigationTarget.get();
      ViewModel viewModel;
      try {
        ObjectEditing.currentNavigationTarget.set(navigationTarget);
        viewModel = (ViewModel) context.getBean(viewModelClass);
        viewModel.initByNavigationTarget(navigationTarget);
        viewModelsByUuid.put(navigationTarget.getUuid(), viewModel);
        UINavigationApiHeadless.uiToOpen.set(navigationTarget);
      } finally {
        ObjectEditing.currentNavigationTarget.set(oldTarget);
      }
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
      Class<T> clazz) {
    T viewModel = super.createAndAddChildViewModel(parent, path, clazz);
    // TODO fix this hack
    viewModelsByUuid.put(viewModel.getViewModelData().getUuid(), viewModel);
    return viewModel;
  }
}
