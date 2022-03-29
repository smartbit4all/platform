package org.smartbit4all.ui.common.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


  private static final Logger log = LoggerFactory.getLogger(UINavigationApiCommon.class);

  protected UserSessionApi userSessionApi;

  /**
   * Don't use directly, use get/putNavigationTargetByUuidInternal
   */
  private Map<UUID, NavigationTarget> navigationTargetsByUUID;

  /**
   * Don't use directly, use get/putContainerByUuidInternal
   */
  private Map<UUID, Container> containersByUUID;

  protected Map<String, NavigableViewDescriptor> navigableViews;
  protected Map<NavigationTargetType, Map<String, NavigableViewDescriptor>> navigableViewsByType;

  protected Map<String, List<SecurityGroup>> securityGroupByView;


  /**
   * Don't use directly, use get/putViewModelByUuidInternal
   */
  private Map<UUID, ViewModel> viewModelsByUuid;


  public static final String UINAVIGATION_CURRENT_NAV_TARGET =
      "UINavigationApi.currentNavigationTarget";

  protected static final String UINAVIGATION_UUID =
      "UINavigationApi.UUID";

  public static final String UINAVIGATION_NAV_TARGETS =
      "UINavigationApi.navigationTargets";

  public static final String UINAVIGATION_VIEW_MODELS =
      "UINavigationApi.viewModels";

  public static final String UINAVIGATION_CONTAINERS =
      "UINavigationApi.containers";

  protected UUID uuid;

  @Autowired
  protected ApplicationContext context;

  private Disposable subscription;

  public UINavigationApiCommon(UserSessionApi userSessionApi) {
    this.userSessionApi = userSessionApi;
    this.uuid = UUID.randomUUID();
    navigationTargetsByUUID = new HashMap<>();
    containersByUUID = new HashMap<>();
    navigableViews = new HashMap<>();
    navigableViewsByType = new HashMap<>();
    securityGroupByView = new HashMap<>();
    viewModelsByUuid = new HashMap<>();
  }

  protected void initSessionParameterListener() {
    Session session = getCurrentSession();
    if (session != null) {
      subscription = session.subscribeForParameterChange(
          UINAVIGATION_CURRENT_NAV_TARGET,
          this::sessionParameterChange);
    }
  }

  private void sessionParameterChange(String paramKey) {
    if (UINAVIGATION_CURRENT_NAV_TARGET.equals(paramKey)) {
      Session session = getCurrentSession();
      if (session != null) {
        navigateToInternal(
            (NavigationTarget) session.getParameter(UINAVIGATION_CURRENT_NAV_TARGET));
      } else {
        log.error("sessionParameterChange when no session present!");
      }
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
    putNavigationTargetByUuidInternal(navigationTarget);;
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
    removeValueFromSessionMap(navigationTargetUuid, UINAVIGATION_CURRENT_NAV_TARGET,
        navigationTargetsByUUID);
    removeValueFromSessionMap(navigationTargetUuid, UINAVIGATION_VIEW_MODELS, viewModelsByUuid);
    removeValueFromSessionMap(navigationTargetUuid, UINAVIGATION_CONTAINERS, containersByUUID);
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

  protected <T extends ViewModel> T createAndInitViewModel(NavigationTarget navigationTarget,
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
  public NavigationTarget getNavigationTargetByUuid(UUID navigationTargetUuid) {
    return getNavigationTargetByUuidInternal(navigationTargetUuid);
  }

  protected void putNavigationTargetByUuidInternal(NavigationTarget navigationTarget) {
    putValueToSessionMap(navigationTarget.getUuid(), navigationTarget,
        UINAVIGATION_NAV_TARGETS, navigationTargetsByUUID);
  }

  protected NavigationTarget getNavigationTargetByUuidInternal(UUID navigationTargetUuid) {
    return getValueFromSessionMap(navigationTargetUuid, UINAVIGATION_NAV_TARGETS,
        navigationTargetsByUUID);
  }

  @Override
  public ViewModel getViewModelByUuid(UUID navigationTargetUuid) {
    ViewModel vm = getViewModelByUuidInternal(navigationTargetUuid);
    return ReflectionUtility.getProxyTarget(vm);
  }

  protected void putViewModelByUuidInternal(UUID uuid, ViewModel viewModel) {
    putValueToSessionMap(uuid, viewModel, UINAVIGATION_VIEW_MODELS, viewModelsByUuid);
  }

  protected ViewModel getViewModelByUuidInternal(UUID navigationTargetUuid) {
    return getValueFromSessionMap(navigationTargetUuid, UINAVIGATION_VIEW_MODELS,
        viewModelsByUuid);
  }

  protected void putContainerByUuidInternal(UUID uuid, Container container) {
    putValueToSessionMap(uuid, container, UINAVIGATION_CONTAINERS, containersByUUID);
  }

  protected Container getContainerByUuidInternal(UUID navigationTargetUuid) {
    return getValueFromSessionMap(navigationTargetUuid, UINAVIGATION_CONTAINERS,
        containersByUUID);
  }

  private <T> void putValueToSessionMap(UUID uuid, T value, String parameterName,
      Map<UUID, T> globalMap) {
    Session session = getCurrentSession();
    if (session != null) {
      session.putValueToMap(uuid, value, parameterName);
    } else {
      globalMap.put(uuid, value);
    }
  }

  private <T> T getValueFromSessionMap(UUID uuid, String parameterName, Map<UUID, T> globalMap) {
    Session session = getCurrentSession();
    if (session != null) {
      return session.getValueFromMap(uuid, parameterName);
    }
    return globalMap.get(uuid);
  }

  private void removeValueFromSessionMap(UUID uuid, String parameterName, Map<UUID, ?> globalMap) {
    Session session = getCurrentSession();
    if (session != null) {
      session.removeEntryFromMap(uuid, parameterName);
    } else {
      globalMap.remove(uuid);
    }
  }

  protected void clearAndRemoveSession() {
    Session session = getCurrentSession();
    if (session != null) {
      session.clearMap(UINAVIGATION_NAV_TARGETS);
      session.clearMap(UINAVIGATION_VIEW_MODELS);
      session.clearMap(UINAVIGATION_CONTAINERS);
      userSessionApi.removeCurrentSession();
    }
  }

  private Session getCurrentSession() {
    return userSessionApi == null ? null : userSessionApi.currentSession();
  }

  @Override
  public void registerContainer(UUID navigationTargetUuid, Container view) {
    putContainerByUuidInternal(navigationTargetUuid, view);
  }
}
