package org.smartbit4all.ui.common.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.api.session.Session;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.Message;
import org.smartbit4all.ui.api.navigation.model.MessageResult;
import org.smartbit4all.ui.api.navigation.model.MessageType;
import org.smartbit4all.ui.api.navigation.model.NavigableViewDescriptor;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;
import org.smartbit4all.ui.api.viewmodel.ViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import com.google.common.base.Strings;
import io.reactivex.rxjava3.disposables.Disposable;

public class UINavigationApiCommon implements UINavigationApi {

  protected UserSessionApi userSessionApi;

  protected Map<UUID, NavigationTarget> navigationTargetsByUUID;

  protected Map<String, NavigableViewDescriptor> navigableViews;
  protected Map<NavigationTargetType, Map<String, NavigableViewDescriptor>> navigableViewsByType;

  protected Map<String, List<SecurityGroup>> securityGroupByView;

  protected Map<UUID, ViewModel> viewModelsByUuid;

  public static final String UINAVIGATION_CURRENT_NAV_TARGET =
      "UINavigationApi.currentNavigationTarget";

  protected static final String UINAVIGATION_UUID =
      "UINavigationApi.UUID";

  protected UUID uuid;

  @Autowired
  protected ApplicationContext context;

  private Disposable subscription;

  public UINavigationApiCommon(UserSessionApi userSessionApi) {
    this.userSessionApi = userSessionApi;
    this.uuid = UUID.randomUUID();
    navigationTargetsByUUID = new HashMap<>();
    navigableViews = new HashMap<>();
    navigableViewsByType = new HashMap<>();
    securityGroupByView = new HashMap<>();
    viewModelsByUuid = new HashMap<>();
  }

  protected void initSessionParameterListener() {
    if (userSessionApi != null && userSessionApi.currentSession() != null) {
      subscription = userSessionApi.currentSession().subscribeForParameterChange(
          UINAVIGATION_CURRENT_NAV_TARGET,
          this::sessionParameterChange);
    }
  }

  private void sessionParameterChange(String paramKey) {
    if (UINAVIGATION_CURRENT_NAV_TARGET.equals(paramKey)) {
      Session session = userSessionApi.currentSession();
      navigateToInternal((NavigationTarget) session.getParameter(UINAVIGATION_CURRENT_NAV_TARGET));
    }
  }

  protected void handleUIDestroyed() {
    if (subscription != null) {
      subscription.dispose();
      subscription = null;
    }
  }

  @Override
  public void navigateTo(NavigationTarget navigationTarget) {
    if (!checkSecurity(navigationTarget)) {
      showSecurityError(navigationTarget);
      return;
    }
    if (navigationTarget.getUuid() == null) {
      navigationTarget.setUuid(UUID.randomUUID());
    }
    navigationTarget.putParametersItem(UINAVIGATION_UUID, uuid);
    navigationTargetsByUUID.put(navigationTarget.getUuid(), navigationTarget);
    if (subscription != null &&
        userSessionApi != null && userSessionApi.currentSession() != null) {
      userSessionApi.currentSession().setParameter(UINAVIGATION_CURRENT_NAV_TARGET,
          navigationTarget);
    } else {
      navigateToInternal(navigationTarget);
    }
  }

  protected void navigateToInternal(NavigationTarget navigationTarget) {}

  @Override
  public void registerView(NavigableViewDescriptor viewDescriptor) {
    navigableViews.put(viewDescriptor.getViewName(), viewDescriptor);
  }

  @Override
  public void registerView(NavigableViewDescriptor viewDescriptor, NavigationTargetType type) {
    navigableViewsByType
        .computeIfAbsent(type, t -> new HashMap<>())
        .put(viewDescriptor.getViewName(), viewDescriptor);
  }

  @Override
  public void close(UUID navigationTargetUuid) {
    navigationTargetsByUUID.remove(navigationTargetUuid);
    viewModelsByUuid.remove(navigationTargetUuid);
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
    securityGroupByView
        .computeIfAbsent(viewName, s -> new ArrayList<>())
        .addAll(Arrays.asList(securityGroups));
  }

  @Override
  public void showMessage(Message message, Consumer<MessageResult> messageListener) {
    if (message.getUuid() == null) {
      message.setUuid(UUID.randomUUID());
    }
  }

  @Override
  public void setTitle(UUID navigationTargetUuid, String title) {
    // TODO Auto-generated method stub
  }

  protected boolean checkSecurity(NavigationTarget navigationTarget) {
    List<SecurityGroup> securityGroups = securityGroupByView.get(navigationTarget.getViewName());
    if (securityGroups == null) {
      return true;
    }
    return securityGroups.stream().anyMatch(SecurityGroup::check);
  }

  protected void showSecurityError(NavigationTarget navigationTarget) {
    String title = calculateTitle(navigationTarget);
    Message securityMessage = new Message()
        .header("Nem megfelelő jogosultság")
        .text("A képernyő megfelelő jogosultság hiányában nem nyitható ki (" + title + ")!")
        .type(MessageType.ERROR);
    showMessage(securityMessage, null);
  }

  protected String calculateTitle(NavigationTarget navigationTarget) {
    String title = navigationTarget.getTitle();
    if (!Strings.isNullOrEmpty(title)) {
      return title;
    }
    NavigableViewDescriptor viewDescriptor = getViewDescriptorByNavigationTarget(navigationTarget);
    if (viewDescriptor != null) {
      title = viewDescriptor.getTitle();
      if (!Strings.isNullOrEmpty(title)) {
        return title;
      }
    }
    return navigationTarget.getViewName();
  }

  protected String calculateIcon(NavigationTarget navigationTarget) {
    String icon = navigationTarget.getIcon();
    if (!Strings.isNullOrEmpty(icon)) {
      return icon;
    }
    NavigableViewDescriptor viewDescriptor = getViewDescriptorByNavigationTarget(navigationTarget);
    icon = viewDescriptor.getIcon();
    if (!Strings.isNullOrEmpty(icon)) {
      return icon;
    }
    return navigationTarget.getIcon();
  }

  @Override
  public <T extends ViewModel> T createViewModel(NavigationTarget navigationTarget,
      Class<T> clazz) {
    NavigationTarget oldTarget = ObjectEditing.currentNavigationTarget.get();
    T viewModel;
    try {
      ObjectEditing.currentNavigationTarget.set(navigationTarget);
      viewModel = context.getBean(clazz);
      viewModel.initByNavigationTarget(navigationTarget);
    } finally {
      ObjectEditing.currentNavigationTarget.set(oldTarget);
    }
    return viewModel;
  }

  @Override
  public <T extends ViewModel> T createAndAddChildViewModel(ViewModel parent, String path,
      Class<T> clazz) {
    NavigationTarget oldTarget = ObjectEditing.currentNavigationTarget.get();
    T viewModel;
    try {
      NavigationTarget navigationTarget = new NavigationTarget()
          .uuid(UUID.randomUUID());
      ObjectEditing.currentNavigationTarget.set(navigationTarget);
      viewModel = context.getBean(clazz);
      parent.addChild(viewModel, path);
    } finally {
      ObjectEditing.currentNavigationTarget.set(oldTarget);
    }
    return viewModel;
  }

  @Override
  public <T> T createView(NavigationTarget navigationTarget, Class<T> clazz) {
    throw new UnsupportedOperationException("CreateView unsupported in ui-common");
  }

  @Override
  public ViewModel getViewModelByUuid(UUID navigationTargetUuid) {
    ViewModel vm = viewModelsByUuid.get(navigationTargetUuid);
    return ReflectionUtility.getProxyTarget(vm);
  }

}
