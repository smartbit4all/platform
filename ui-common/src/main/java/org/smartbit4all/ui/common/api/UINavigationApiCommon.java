package org.smartbit4all.ui.common.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;

public class UINavigationApiCommon implements UINavigationApi {

  protected Map<UUID, NavigationTarget> navigationTargetsByUUID;

  protected Map<String, NavigableViewDescriptor> navigableViews;

  public UINavigationApiCommon() {
    navigationTargetsByUUID = new HashMap<>();
    navigableViews = new HashMap<>();
  }

  @Override
  public void navigateTo(NavigationTarget navigationTarget) {
    if (navigationTarget.getUuid() == null) {
      navigationTarget.setUuid(UUID.randomUUID());
    }
    navigationTargetsByUUID.put(navigationTarget.getUuid(), navigationTarget);
  }

  @Override
  public void registerView(NavigableViewDescriptor viewDescriptor) {
    navigableViews.put(viewDescriptor.getViewName(), viewDescriptor);
  }

  @Override
  public void close(UUID navigationTargetUuid) {
    navigationTargetsByUUID.remove(navigationTargetUuid);
  }

}
