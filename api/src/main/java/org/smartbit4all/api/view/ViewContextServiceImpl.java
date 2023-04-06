package org.smartbit4all.api.view;

import java.lang.annotation.Annotation;
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
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.exception.ViewContextMissigException;
import org.smartbit4all.api.view.annotation.BeforeClose;
import org.smartbit4all.api.view.annotation.MessageHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.CloseResult;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.MessageResult;
import org.smartbit4all.api.view.bean.OpenPendingData;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextData;
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
import static java.util.stream.Collectors.toList;

public class ViewContextServiceImpl implements ViewContextService {

  private static final Logger log = LoggerFactory.getLogger(ViewContextServiceImpl.class);

  private static final ThreadLocal<ViewContext> currentViewContext = new ThreadLocal<>();

  private Map<String, String> parentViewByViewName = new HashMap<>();

  private Map<String, Object> apiByViewName = new HashMap<>();

  private Map<String, Boolean> keepModelImplicitByViewName = new HashMap<>();

  private Map<String, Class<?>> modelClassByViewName = new HashMap<>();

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

  @Autowired
  private CollectionApi collectionApi;

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
  public ViewContextData createViewContext() {
    UUID uuid = UUID.randomUUID();
    ViewContext viewContext = new ViewContext()
        .uuid(uuid);
    URI uri = objectApi.saveAsNew(SCHEMA, viewContext);
    log.debug("Viewcontext created: uuid={}, uri={}", uuid, uri);
    sessionApi.addViewContext(uuid, uri);
    return convertContextToUi(
        objectApi.load(uri).getObject(ViewContext.class));
  }

  private ViewContextData convertContextToUi(ViewContext context) {
    return new ViewContextData()
        .uuid(context.getUuid())
        .views(context.getViews().stream()
            .map(this::convertViewToUi)
            .collect(toList()));
  }

  private ViewData convertViewToUi(View view) {
    return new ViewData()
        .uuid(view.getUuid())
        .containerUuid(view.getContainerUuid())
        .viewName(view.getViewName())
        .type(view.getType())
        .state(view.getState())
        .message(objectApi.asType(
            MessageData.class,
            view.getParameters().get(ViewApiImpl.MESSAGE_DATA)));
  }

  @Override
  public ViewContextData getCurrentViewContext() {
    return convertContextToUi(getCurrentViewContextEntry());
  }

  @Override
  public ViewContext getCurrentViewContextEntry() {
    checkIfViewContextAvailable();
    return currentViewContext.get();
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
    return currentViewContext.get().getUuid();
  }

  @Override
  public ViewContextData getViewContext(UUID uuid) {
    UUID currentUuid = getCurrentViewContextUuid();
    if (!Objects.equals(uuid, currentUuid)) {
      throw new IllegalArgumentException("currentViewContext doesn't match paramater");
    }
    return getCurrentViewContext();
  }

  @Override
  public void updateCurrentViewContext(UnaryOperator<ViewContext> update) {
    checkIfViewContextAvailable();
    currentViewContext.set(update.apply(getCurrentViewContextEntry()));
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
  public Boolean getKeepModelOnImplicitClose(String viewName) {
    Boolean result = keepModelImplicitByViewName.get(viewName);
    return result == null ? Boolean.FALSE : result;
  }

  @Override
  public View getViewFromCurrentViewContext(UUID viewUuid) {
    ViewContext viewContext = getCurrentViewContextEntry();
    return ViewContexts.getView(viewContext, viewUuid);
  }

  @Override
  public View getViewFromCurrentSession(UUID viewUuid) {
    return sessionApi.getViewContexts().values().stream()
        .map(u -> objectApi.read(u, ViewContext.class))
        .flatMap(vc -> vc.getViews().stream()).filter(v -> viewUuid.equals(v.getUuid())).findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "Unable to identify the (" + viewUuid + ") view in the session"));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <M> M getModel(UUID viewUuid, Class<M> clazz) {
    View view = getViewFromCurrentViewContext(viewUuid);
    Objects.requireNonNull(view, "View not found!");
    if (clazz == null) {
      clazz = (Class<M>) modelClassByViewName.get(view.getViewName());
      if (clazz == null) {
        throw new IllegalArgumentException(
            "View is not PageApi and model clazz is not specified! " + view.getViewName());
      }
    }
    Object modelObject = view.getModel();
    boolean modelWasEmpty = modelObject == null;
    if (modelWasEmpty) {
      String viewName = view.getViewName();
      Object api = apiByViewName.get(viewName);
      Objects.requireNonNull(api, "API not found for view " + viewName);
      if (!(api instanceof PageApi)) {
        log.warn("View getModel called, but it's api is not PageApi: {} ({})",
            viewName, api.getClass().getName());
        return null;
      }
      modelObject = ((PageApi<?>) api).initModel(view);
      view.setModel(modelObject);
    }
    if (clazz.isInstance(modelObject)) {
      if (modelWasEmpty) {
        view.putParametersItem(ViewContexts.INITIAL_MODEL, modelObject);
      }
      return (M) modelObject;
    }
    M model = objectApi.asType(clazz, view.getModel());
    // this is to ensure View holds a typed object, not a Map representing the object
    view.setModel(model);
    if (modelWasEmpty) {
      view.putParametersItem(ViewContexts.INITIAL_MODEL, modelObject);
    }
    return model;
  }

  @Override
  public void handleMessage(UUID viewUuid, UUID messageUuid, MessageResult messageResult) {
    Objects.requireNonNull(messageResult, "MessageResult must be specified");
    Objects.requireNonNull(messageResult.getSelectedOption(),
        "MessageResult.selectedOption must be specified");
    Objects.requireNonNull(messageResult.getSelectedOption().getCode(),
        "MessageResult.selectedOption.code must be specified");

    View message = getViewFromCurrentViewContext(messageUuid);
    Objects.requireNonNull(message, "Message not found!");
    View view = getViewFromCurrentViewContext(viewUuid);
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
    context.getBeansWithAnnotation(ViewApi.class).values()
        .forEach(this::setupScreenApi);
  }

  private void setupScreenApi(Object api) {
    ReflectionUtility.getAnnotationsByType(
        api.getClass(),
        ViewApi.class)
        .forEach(view -> registerView(view, api));
  }

  private void registerView(ViewApi view, Object api) {
    String viewName = view.value();
    if (apiByViewName.containsKey(viewName)) {
      throw new IllegalStateException("View already registered! " + viewName);
    }
    apiByViewName.put(viewName, api);
    keepModelImplicitByViewName.put(viewName, view.keepModelOnImplicitClose());
    parentViewByViewName.put(viewName, view.parent());
    if (api instanceof PageApi) {
      modelClassByViewName.put(viewName, ((PageApi<?>) api).getClazz());
    }
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
    registerAnnotatedMethods(viewName, api, BeforeClose.class, beforeCloseMethodsByView);
  }

  private void registerMessageMethods(String viewName, Object api) {
    Map<String, Method> messageMethods = new HashMap<>();
    ReflectionUtility.allMethods(
        api.getClass(),
        method -> method.isAnnotationPresent(MessageHandler.class))
        .forEach(method -> collectMessageMethod(viewName, method, messageMethods));

    messageMethodsByView.put(viewName, messageMethods);
  }

  private void registerAnnotatedMethods(String viewName, Object api,
      Class<? extends Annotation> annotation, Map<String, Method> methodsByView) {
    Set<Method> methods = ReflectionUtility.allMethods(
        api.getClass(),
        method -> method.isAnnotationPresent(annotation));
    if (methods.size() > 1) {
      throw new IllegalArgumentException(
          "More than 1 @" + annotation.getName() + " method in " + viewName);
    }
    if (methods.size() == 1) {
      methodsByView.put(viewName, methods.iterator().next());
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
    View viewToClose = getViewFromCurrentViewContext(viewToCloseUuid);
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
      throw new ViewContextMissigException();
    }
    StorageObjectLock lock = storage.get().getLock(viewContextUri);
    lock.lock();
    try {
      ObjectNode contextNode = objectApi.load(viewContextUri);
      currentViewContext.set(contextNode.getObject(ViewContext.class));
      command.execute();
      contextNode.modify(ViewContext.class, c -> currentViewContext.get());
      objectApi.save(contextNode);
    } finally {
      lock.unlock();
      currentViewContext.remove();
    }
  }

}
