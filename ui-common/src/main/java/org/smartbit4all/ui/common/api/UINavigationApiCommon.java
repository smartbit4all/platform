package org.smartbit4all.ui.common.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.Message;
import org.smartbit4all.ui.api.navigation.model.MessageResult;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;

public class UINavigationApiCommon implements UINavigationApi {

  protected Map<UUID, NavigationTarget> navigationTargetsByUUID;

  protected Map<String, NavigableViewDescriptor> navigableViews;
  protected Map<NavigationTargetType, Map<String, NavigableViewDescriptor>> navigableViewsByType;

  protected Map<String, List<SecurityGroup>> securityGroupByView;

  public UINavigationApiCommon() {
    navigationTargetsByUUID = new HashMap<>();
    navigableViews = new HashMap<>();
    navigableViewsByType = new HashMap<>();
    securityGroupByView = new HashMap<>();
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
        NavigableViewDescriptor viewDescriptor =
            viewDescriptors.get(navigationTarget.getViewName());
        if (viewDescriptor != null) {
          return viewDescriptor;
        }
      }
    }
    return navigableViews.get(navigationTarget.getViewName());
  }

  @Override
  public NavigationTarget getNavigationTargetByUuid(UUID navigationTargetUuid) {
    return navigationTargetsByUUID.get(navigationTargetUuid);
  }

  @Override
  public void registerSecurityGroup(String viewName, SecurityGroup... securityGroups) {
    List<SecurityGroup> groupList = securityGroupByView.get(viewName);
    if (groupList == null) {
      groupList = new ArrayList<>();
      securityGroupByView.put(viewName, groupList);
    }
    groupList.addAll(Arrays.asList(securityGroups));
  }

  @Override
  public void showMessage(Message message, Consumer<MessageResult> messageListener) {
    // TODO Auto-generated method stub
  }

  @Override
  public void setTitle(UUID navigationTargetUuid, String title) {
    // TODO Auto-generated method stub
  }
}
