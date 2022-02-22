package org.smartbit4all.ui.common.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;
import org.smartbit4all.ui.api.viewmodel.ViewModel;

public class UINavigationApiHeadless extends UINavigationApiCommon {

  private static final Logger log = LoggerFactory.getLogger(UINavigationApiHeadless.class);

  private NavigationTarget uiToOpen;

  public UINavigationApiHeadless(UserSessionApi userSessionApi) {
    super(userSessionApi);
  }

  @Override
  protected void navigateToInternal(NavigationTarget navigationTarget) {
    NavigableViewDescriptor desc = getViewDescriptorByNavigationTarget(navigationTarget);
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
        this.uiToOpen = navigationTarget;
      } finally {
        ObjectEditing.currentNavigationTarget.set(oldTarget);
      }
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(
          "ViewClass not found for view" + navigationTarget.getViewName());
    }

  }

  public NavigationTarget getUiToOpen() {
    return uiToOpen;
  }

  public void clearUiToOpen() {
    this.uiToOpen = null;
  }

}
