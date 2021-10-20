package org.smartbit4all.ui.api.navigation;

import java.util.UUID;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;

public interface UINavigationApi {

  void registerView(NavigableViewDescriptor viewDescriptor);

  void navigateTo(NavigationTarget navigationTarget);

  void close(UUID navigationTargetUuid);

}
