package org.smartbit4all.ui.api.navigation;

import java.util.UUID;
import java.util.function.Consumer;
import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.ui.api.navigation.model.Message;
import org.smartbit4all.ui.api.navigation.model.MessageResult;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;

public interface UINavigationApi {

  void registerView(NavigableViewDescriptor viewDescriptor);

  void registerView(NavigableViewDescriptor viewDescriptor, NavigationTargetType type);

  void registerSecurityGroup(String viewName, SecurityGroup securityGroup);

  void navigateTo(NavigationTarget navigationTarget);

  void close(UUID navigationTargetUuid);

  void showMessage(Message message, Consumer<MessageResult> messageListener);

  void setTitle(UUID navigationTargetUuid, String title);

}
