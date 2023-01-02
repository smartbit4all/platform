package org.smartbit4all.api.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.bean.CloseResult;
import org.smartbit4all.api.view.bean.MessageResult;
import org.smartbit4all.api.view.bean.OpenPendingData;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
import org.smartbit4all.api.view.bean.ViewData;
import org.smartbit4all.api.view.bean.ViewState;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;

public class ViewContextServiceImpl implements ViewContextService {

  private static final Logger log = LoggerFactory.getLogger(ViewContextServiceImpl.class);

  private static final ThreadLocal<ObjectNode> currentViewContext = new ThreadLocal<>();

  private Map<String, String> parentViewByViewName = new HashMap<>();

  private Map<String, Object> apiByViewName = new HashMap<>();

  private Map<String, Map<String, Method>> messageMethodsByView = new HashMap<>();

  private Map<String, Method> beforeCloseMethodsByView = new HashMap<>();

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private ApplicationContext context;

  private Supplier<Storage> storage = new Supplier<Storage>() {

    private Storage storageInstance;

    @Override
    public Storage get() {
      if (storageInstance == null) {
        storageInstance = storageApi.get(SCHEMA);
        storageInstance.setVersionPolicy(VersionPolicy.SINGLEVERSION);
      }
      return storageInstance;
    }
  };

  @Override
  public ViewContext createViewContext() {
    UUID uuid = UUID.randomUUID();
    ViewContext viewContext = new ViewContext()
        .uuid(uuid);
    URI uri = objectApi.saveAsNew(SCHEMA, viewContext);
    log.debug("Viewcontext created: uuid={}, uri={}", uuid, uri);
    sessionApi.addViewContext(uuid, uri);
    return objectApi.load(uri).getObject(ViewContext.class);
  }

  @Override
  public ViewContext getCurrentViewContext() {
    checkIfViewContextAvailable();
    return currentViewContext.get().getObject(ViewContext.class);
  }

  private void checkIfViewContextAvailable() {
    if (currentViewContext.get() == null) {
      throw new IllegalStateException(
          "currentViewContext is not set, please use ViewContextService.execute() to use ViewContext");
    }
  }

  @Override
  public UUID getCurrentViewContextUuid() {
    checkIfViewContextAvailable();
    return currentViewContext.get().getValue(UUID.class, ViewContext.UUID);
  }

  @Override
  public ViewContext getViewContext(UUID uuid) {
    UUID currentUuid = getCurrentViewContextUuid();
    if (!currentUuid.equals(uuid)) {
      throw new IllegalArgumentException("currentViewContext doesn't match paramater");
    }
    return getCurrentViewContext();
  }

  @Override
  public void updateCurrentViewContext(UnaryOperator<ViewContext> update) {
    checkIfViewContextAvailable();
    currentViewContext.get().modify(ViewContext.class, update);
  }

  @Override
  public void updateViewContext(ViewContextUpdate updates) {
    checkIfViewContextAvailable();
    if (!Objects.equals(updates.getUuid(), getCurrentViewContextUuid())) {
      throw new IllegalArgumentException("currentViewContext doesn't match paramater");
    }
    if (!updates.getUpdates().stream().allMatch(
        update -> update.getState() == ViewState.OPENED
            || update.getState() == ViewState.CLOSED)) {
      throw new IllegalArgumentException("Only OPENED and CLOSED updates allowed");
    }
    updateCurrentViewContext(
        c -> {
          updates.getUpdates().forEach(u -> ViewContexts.updateViewState(c, u));
          c.getViews().removeIf(v -> ViewState.CLOSED == v.getState());
          return c;
        });
  }

  @Override
  public String getParentViewName(String viewName) {
    String result = parentViewByViewName.get(viewName);
    return result == null ? "" : result;
  }

  @Override
  public ViewData getViewFromCurrentViewContext(UUID viewUuid) {
    ViewContext viewContext = getCurrentViewContext();
    return ViewContexts.getView(viewContext, viewUuid);
  }

  @Override
  public void handleMessage(UUID viewUuid, UUID messageUuid, MessageResult messageResult) {
    Objects.requireNonNull(messageResult, "MessageResult must be specified");
    Objects.requireNonNull(messageResult.getSelectedOption(),
        "MessageResult.selectedOption must be specified");
    Objects.requireNonNull(messageResult.getSelectedOption().getCode(),
        "MessageResult.selectedOption.code must be specified");

    ViewData message = getViewFromCurrentViewContext(messageUuid);
    Objects.requireNonNull(message, "Message not found!");
    ViewData view = getViewFromCurrentViewContext(viewUuid);
    Object api = apiByViewName.get(view.getViewName());
    Objects.requireNonNull(api, "API not found for view " + view.getViewName());
    Map<String, Method> messageMethods = messageMethodsByView.get(view.getViewName());
    if (messageMethods == null) {
      return;
    }

    String code = messageResult.getSelectedOption().getCode();
    Method method = messageMethods.get(code);
    if (method == null) {
      // wildcard handler
      method = messageMethods.get("");
    }
    if (method != null) {
      try {
        // TODO examine signature, try to support many variations
        method.invoke(api, viewUuid, messageUuid, messageResult);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        log.error("Error when calling MessageHandler method " + method.getName(), e);
      }
    }
    updateCurrentViewContext(
        c -> ViewContexts.updateViewState(c, messageUuid, ViewState.TO_CLOSE));
  }

  @EventListener(ApplicationStartedEvent.class)
  private void initViews(ApplicationStartedEvent applicationPreparedEvent) {
    context.getBeansWithAnnotation(View.class).values()
        .forEach(this::setupScreenApi);
  }

  private void setupScreenApi(Object api) {
    ReflectionUtility.getAnnotationsByType(
        api.getClass(),
        View.class)
        .forEach(view -> registerView(view, api));
  }

  private void registerView(View view, Object api) {
    String viewName = view.value();
    if (apiByViewName.containsKey(viewName)) {
      throw new IllegalStateException("View already registered! " + viewName);
    }
    apiByViewName.put(viewName, api);
    parentViewByViewName.put(viewName, view.parent());
    registerViewMethods(viewName, api);
  }

  /**
   * Register all message and navigation handling methods of API class for given view.
   * 
   * @param viewName
   * @param api
   */
  private void registerViewMethods(String viewName, Object api) {
    registerMessageMethods(viewName, api);
    registerBeforeCloseMethods(viewName, api);
  }

  private void registerMessageMethods(String viewName, Object api) {
    Map<String, Method> messageMethods = new HashMap<>();
    ReflectionUtility.allMethods(
        api.getClass(),
        method -> method.isAnnotationPresent(MessageHandler.class))
        .forEach(method -> collectMessageMethod(viewName, method, messageMethods));

    messageMethodsByView.put(viewName, messageMethods);
  }

  private void registerBeforeCloseMethods(String viewName, Object api) {
    Set<Method> methods = ReflectionUtility.allMethods(
        api.getClass(),
        method -> method.isAnnotationPresent(BeforeClose.class));
    if (methods.size() > 1) {
      throw new IllegalArgumentException("More than 1 @BeforeCloseEvent method in " + viewName);
    }
    if (methods.size() == 1) {
      beforeCloseMethodsByView.put(viewName, methods.iterator().next());
    }
  }

  /**
   * Process method's {@link MessageHandler} annotation values: for each value, put an entry to
   * messageMethods with value as key and method as value.
   * 
   * @param method
   * @param messageMethods
   */
  private void collectMessageMethod(String viewName, Method method,
      Map<String, Method> messageMethods) {
    MessageHandler annotation = AnnotationUtils.findAnnotation(method, MessageHandler.class);
    if (annotation != null) {
      List<String> messages = Arrays.asList(annotation.value());
      for (String message : messages) {
        if (messageMethods.containsKey(message)) {
          throw new IllegalStateException("MessageHandler duplicated! " + viewName + "." + message);
        }
        messageMethods.put(message, method);
      }
    }
  }

  @Override
  public CloseResult callBeforeClose(UUID viewToCloseUuid, OpenPendingData data) {
    ViewData viewToClose = getViewFromCurrentViewContext(viewToCloseUuid);
    Objects.requireNonNull(viewToClose, "View not found!");
    String viewName = viewToClose.getViewName();
    Object api = apiByViewName.get(viewName);
    Objects.requireNonNull(api, "API not found for view " + viewName);
    Method method = beforeCloseMethodsByView.get(viewName);
    CloseResult result = CloseResult.APPROVED;
    if (method != null) {
      try {
        result = (CloseResult) method.invoke(api, viewToCloseUuid, data);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        log.error("Error when calling BeforeClose method" + method.getName(), e);
      }
    }
    return result;
  }

  @Override
  public void execute(UUID uuid, ViewContextCommand command) throws Exception {
    Objects.requireNonNull(uuid, "currentViewContextUuid is not set");
    Objects.requireNonNull(command, "command is not set");
    URI viewContextUri = sessionApi.getViewContexts().get(uuid.toString());
    if (viewContextUri == null) {
      throw new IllegalArgumentException("ViewContext not found by UUID");
    }
    StorageObjectLock lock = storage.get().getLock(viewContextUri);
    lock.lock();
    try {
      currentViewContext.set(objectApi.load(viewContextUri));
      command.execute();
      objectApi.save(currentViewContext.get());
    } finally {
      lock.unlockAndRelease();
      currentViewContext.remove();
    }
  }

}
