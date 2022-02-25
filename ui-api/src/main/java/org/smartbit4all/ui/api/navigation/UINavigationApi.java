package org.smartbit4all.ui.api.navigation;

import java.util.UUID;
import java.util.function.Consumer;
import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.ui.api.navigation.model.Message;
import org.smartbit4all.ui.api.navigation.model.MessageResult;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;
import org.smartbit4all.ui.api.viewmodel.ViewModel;

public interface UINavigationApi {

  void registerView(NavigableViewDescriptor viewDescriptor);

  void registerView(NavigableViewDescriptor viewDescriptor, NavigationTargetType type);

  /**
   * View identified by viewName will be available if any securityGroup.check returns true.
   * 
   * @param viewName
   * @param securityGroups
   */
  void registerSecurityGroup(String viewName, SecurityGroup... securityGroups);

  void navigateTo(NavigationTarget navigationTarget);

  void close(UUID navigationTargetUuid);

  void showMessage(Message message, Consumer<MessageResult> messageListener);

  void setTitle(UUID navigationTargetUuid, String title);

  NavigationTarget getNavigationTargetByUuid(UUID navigationTargetUuid);

  <T extends ViewModel> T createViewModel(NavigationTarget navigationTarget, Class<T> clazz);

  <T extends ViewModel> T createAndAddChildViewModel(ViewModel parent, String path, Class<T> clazz);

  <T> T createView(NavigationTarget navigationTarget, Class<T> clazz);

  ViewModel getViewModelByUuid(UUID navigationTargetUuid);

}
