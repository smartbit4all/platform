package org.smartbit4all.ui.common.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;

public abstract class UINavigationApiCommon implements UINavigationApi {

  protected Map<UUID, NavigationTarget> navigationTargetsByUUID;

  protected Map<String, NavigableViewDescriptor> navigableViews;
  protected Map<NavigationTargetType, Map<String, NavigableViewDescriptor>> navigableViewsByType;

  public UINavigationApiCommon() {
    navigationTargetsByUUID = new HashMap<>();
    navigableViews = new HashMap<>();
    navigableViewsByType = new HashMap<>();
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
  public void registerView(NavigableViewDescriptor viewDescriptor, NavigationTargetType type) {
    Map<String, NavigableViewDescriptor> viewsByName = navigableViewsByType.get(type);
    if (viewsByName == null) {
      viewsByName = new HashMap<>();
      navigableViewsByType.put(type, viewsByName);
    }
    viewsByName.put(viewDescriptor.getViewName(), viewDescriptor);
  }

  @Override
  public void close(UUID navigationTargetUuid) {
    navigationTargetsByUUID.remove(navigationTargetUuid);
  }

  protected NavigableViewDescriptor getViewDescriptorByNavigationTarget(
      NavigationTarget navigationTarget) {
    if (navigationTarget.getType() != null) {
      Map<String, NavigableViewDescriptor> viewDescriptors =
          navigableViewsByType.get(navigationTarget.getType());
      if (viewDescriptors != null) {
        NavigableViewDescriptor viewDescriptor = viewDescriptors.get(navigationTarget.getViewName());
        if (viewDescriptor != null) {
          return viewDescriptor;
        }
      }
    }
    return navigableViews.get(navigationTarget.getViewName());
  }
}
