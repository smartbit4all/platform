package org.smartbit4all.api.view;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContext;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContextObject;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.exception.ViewContextMissigException;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.BeforeClose;
import org.smartbit4all.api.view.annotation.MessageHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.CloseResult;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.ComponentModel;
import org.smartbit4all.api.view.bean.ComponentModelChange;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.MessageResult;
import org.smartbit4all.api.view.bean.OpenPendingData;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextChange;
import org.smartbit4all.api.view.bean.ViewContextData;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
import org.smartbit4all.api.view.bean.ViewData;
import org.smartbit4all.api.view.bean.ViewEventHandler;
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
import static java.util.stream.Collectors.toMap;

public class ViewContextServiceImpl implements ViewContextService {

  public interface ViewCall {
    void run() throws RuntimeException;
  }

  private static final Logger log = LoggerFactory.getLogger(ViewContextServiceImpl.class);

  private static final ThreadLocal<ViewContext> currentViewContext = new ThreadLocal<>();

  private Map<String, String> parentViewByViewName = new HashMap<>();

  private Map<String, Object> apiByViewName = new HashMap<>();

  private Map<String, Boolean> keepModelImplicitByViewName = new HashMap<>();

  private Map<String, Class<?>> modelClassByViewName = new HashMap<>();

  /**
   * <View.name <MessageHandler.value <Method>>>
   *
   */
  private Map<String, Map<String, Method>> messageMethodsByView = new HashMap<>();

  /**
   * <View.name <ActionHandler.value <Method>>>
   *
   */
  private Map<String, Map<String, Method>> actionMethodsByView = new HashMap<>();

  /**
   * <View.name <WidgetActionHandler.widget <WidgetActionHandler.value <Method>>>>
   *
   */
  private Map<String, Map<String, Map<String, Method>>> widgetActionMethodsByViewAndWidget =
      new HashMap<>();

  private Map<String, Method> beforeCloseMethodsByView = new HashMap<>();

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private InvocationApi invocationApi;

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

    View message;
    try {
      message = getViewFromCurrentViewContext(messageUuid);
      Objects.requireNonNull(message, "Message not found!");
    } catch (Exception e) {
      // message not found -> log error and remove message to avoid infinite loop
      log.error("Unexpected error when retreiving message", e);
      updateCurrentViewContext(
          c -> ViewContexts.updateViewState(c, messageUuid, ViewState.TO_CLOSE));
      return;
    }
    View view;
    try {
      view = getViewFromCurrentViewContext(viewUuid);
    } catch (Exception e) {
      // view not found -> log error and remove message to avoid infinite loop
      log.error("Unexpected error when retreiving message's view", e);
      updateCurrentViewContext(
          c -> ViewContexts.updateViewState(c, messageUuid, ViewState.TO_CLOSE));
      return;
    }
    Object api = apiByViewName.get(view.getViewName());
    Objects.requireNonNull(api, "API not found for view " + view.getViewName());
    Method method = getMethodForCode(messageMethodsByView, view.getViewName(),
        messageResult.getSelectedOption().getCode());
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
    ctx.getBeansWithAnnotation(ViewApi.class).values()
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
    registerAnnotatedMethodsWithCode(viewName, api, MessageHandler.class, messageMethodsByView,
        annotation -> Arrays.asList(annotation.value()));
    registerAnnotatedMethodsWithCode(viewName, api, ActionHandler.class, actionMethodsByView,
        annotation -> Arrays.asList(annotation.value()));
    registerWidgetActionMethods(viewName, api);
    registerAnnotatedMethods(viewName, api, BeforeClose.class, beforeCloseMethodsByView);
  }


  private void registerWidgetActionMethods(String viewName, Object api) {
    Map<String, Map<String, Method>> widgetMethods = new HashMap<>();
    ReflectionUtility.allMethods(
        api.getClass(),
        method -> method.isAnnotationPresent(WidgetActionHandler.class))
        .forEach(method -> collectWidgetActionMethod(viewName, method, widgetMethods));

    widgetActionMethodsByViewAndWidget.put(viewName, widgetMethods);
  }

  private void collectWidgetActionMethod(String viewName, Method method,
      Map<String, Map<String, Method>> methods) {
    WidgetActionHandler annotation =
        AnnotationUtils.findAnnotation(method, WidgetActionHandler.class);
    if (annotation != null) {
      List<String> widgets = Arrays.asList(annotation.widget());
      List<String> actions = Arrays.asList(annotation.value());

      for (String widget : widgets) {
        Map<String, Method> methodsForWidget =
            methods.computeIfAbsent(widget, w -> new HashMap<>());
        for (String action : actions) {
          if (methodsForWidget.containsKey(action)) {
            throw new IllegalStateException("WIdgetActionHandler duplicated! "
                + viewName + "." + widget + "." + action);
          }
          methodsForWidget.put(action, method);
        }
      }
    }
  }

  private <T extends Annotation> void registerAnnotatedMethodsWithCode(String viewName, Object api,
      Class<T> annotationClass, Map<String, Map<String, Method>> methodsByView,
      Function<T, List<String>> valueExtractor) {
    Map<String, Method> methods = new HashMap<>();
    ReflectionUtility.allMethods(
        api.getClass(),
        method -> method.isAnnotationPresent(annotationClass))
        .forEach(method -> {
          T annotation = AnnotationUtils.findAnnotation(method, annotationClass);
          if (annotation != null) {
            List<String> codes = valueExtractor.apply(annotation);
            for (String code : codes) {
              if (methods.containsKey(code)) {
                throw new IllegalStateException(
                    annotationClass.getSimpleName() + "Handler duplicated! " + viewName + "."
                        + code);
              }
              methods.put(code, method);
            }
          }
        });

    methodsByView.put(viewName, methods);

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

  @Override
  public ComponentModel getComponentModel(UUID viewUuid) {
    View view = getViewFromCurrentViewContext(viewUuid);
    Objects.requireNonNull(view, "View not found!");
    Object data = getModel(viewUuid, null);
    List<ComponentConstraint> constraints;
    if (view.getConstraint() != null) {
      constraints = view.getConstraint().getComponentConstraints();
    } else {
      constraints = Collections.emptyList();
    }
    return new ComponentModel()
        .uuid(view.getUuid())
        .name(view.getViewName())
        .data(data)
        .constraints(constraints)
        .actions(view.getActions())
        .valueSets(view.getValueSets())
        .widgetModels(view.getWidgetModels())
        .layouts(view.getLayouts());
  }

  @Override
  public ViewContextChange performAction(UUID viewUuid, UiActionRequest request) {
    Objects.requireNonNull(request, "Request must be specified!");
    Objects.requireNonNull(request.getCode(), "Request.code must be specified!");
    View view = getViewFromCurrentViewContext(viewUuid);
    Objects.requireNonNull(view, "View not found!");
    Object api = apiByViewName.get(view.getViewName());
    Objects.requireNonNull(api, "API not found for view " + view.getViewName());
    Method method = getMethodForCode(actionMethodsByView, view.getViewName(), request.getCode());
    ViewEventApi eventApiImpl = new ViewEventApiImpl(view);
    ViewEventDescriptor eventDescriptor =
        eventApiImpl.get(ViewEventApi.ACTION, request.getCode());
    if (method == null && eventDescriptor.getInsteadOf() == null) {
      throw new IllegalStateException("No actionHandler for request! " + request);
    }
    ObjectPropertyResolverContext resolverContext =
        new ObjectPropertyResolverContext().addObjectsItem(
            new ObjectPropertyResolverContextObject().name("model").uri(view.getObjectUri()));
    return invokeMethodInternal(eventDescriptor, resolverContext, method, api, viewUuid, request);
  }

  @Override
  public ViewContextChange performWidgetAction(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    Objects.requireNonNull(request, "Request must be specified!");
    Objects.requireNonNull(request.getCode(), "Request.code must be specified!");
    Objects.requireNonNull(widgetId, "WidgetId must be specified!");
    View view = getViewFromCurrentViewContext(viewUuid);
    Objects.requireNonNull(view, "View not found!");
    Object api = apiByViewName.get(view.getViewName());
    Objects.requireNonNull(api, "API not found for view " + view.getViewName());
    Method method = getMethodForWidgetAndCode(widgetActionMethodsByViewAndWidget,
        view.getViewName(), widgetId, request.getCode());
    ViewEventApi eventApiImpl = new ViewEventApiImpl(view);
    ViewEventDescriptor eventDescriptor =
        eventApiImpl.get(ViewEventApi.WIDGET, widgetId, request.getCode());
    if (method == null && eventDescriptor.getInsteadOf() == null) {
      throw new IllegalStateException("No actionHandler for request! " + request);
    }
    ObjectPropertyResolverContext resolverContext =
        new ObjectPropertyResolverContext();
    // TODO Resolve to use a ObjectNode instead of an URI. In this case we use view.getModel() or
    // other part of the view directly.
    // .addObjectsItem(
    // new ObjectPropertyResolverContextObject().name("model").uri(view.getObjectUri()));
    return invokeMethodInternal(eventDescriptor, resolverContext, method, api, viewUuid, widgetId,
        nodeId, request);
  }

  /**
   * @param method Runnable is used here as a void -> void functional interface, this is the method
   *        invocation.
   * @return
   */
  private ViewContextChange invokeMethodInternal(ViewEventDescriptor eventDescriptor,
      ObjectPropertyResolverContext resolverContext, Method method,
      Object api, Object... args) {
    InvocationRequest insteadOfRequest = null;
    if (eventDescriptor.getInsteadOf() != null) {
      // TODO resolve with the args.
      insteadOfRequest = invocationApi.resolve(
          eventDescriptor.getInsteadOf().getInvocationRequestDefinition(), resolverContext);
    }

    ObjectNode before = beforeInvoke(getMethodName(insteadOfRequest, method));
    InvocationRequest invocationRequest = null;
    try {
      // The before events can block the execution of the whole action. If an invocation throws
      // exception then the execution is interrupted. If we would like to continue the execution of
      // TODO the before events then we have to throw some special exception or so.
      for (ViewEventHandler eventHandler : eventDescriptor.getBeforeEvents()) {
        invocationRequest = invocationApi.resolve(
            eventHandler.getInvocationRequestDefinition(), resolverContext);
        try {
          invocationApi.invoke(invocationRequest);
        } catch (ApiNotFoundException e) {
          log.error("Unable to call " + invocationRequest, e);
          throw new IllegalAccessException(e.getMessage());
        }
      }

      if (insteadOfRequest == null) {
        method.invoke(api, args);
      } else {
        try {
          invocationRequest = insteadOfRequest;
          invocationApi.invoke(invocationRequest);
        } catch (ApiNotFoundException e) {
          log.error("Unable to call " + invocationRequest, e);
          throw new IllegalAccessException(e.getMessage());
        }
      }

      // These event won't block the execution. Their exceptions and error are logged but the
      // execution continues.
      for (ViewEventHandler eventHandler : eventDescriptor.getAfterEvents()) {
        invocationRequest = invocationApi.resolve(
            eventHandler.getInvocationRequestDefinition(), resolverContext);
        try {
          invocationApi.invoke(invocationRequest);
        } catch (Exception e) {
          log.error("Unable to call " + invocationRequest, e);
        }
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(
          "IllegalAccessException when calling method " + getMethodName(invocationRequest, method),
          e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(
          "IllegalArgumentException when calling method "
              + getMethodName(invocationRequest, method),
          e);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      }
      throw new RuntimeException(
          "InvocationTargetException without cause calling method "
              + getMethodName(invocationRequest, method),
          e);
    }
    return afterInvoke(before, getMethodName(invocationRequest, method));
  }

  private final String getMethodName(InvocationRequest request, Method method) {
    return request != null ? request.toString() : (method != null ? method.getName() : "unknown");
  }

  private ObjectNode beforeInvoke(String methodName) {
    try {
      return objectApi.create(SCHEMA, getCurrentViewContextEntry());
    } catch (Throwable tr) {
      throw new RuntimeException("Error after calling method " + methodName,
          tr);
    }
  }

  private ViewContextChange afterInvoke(ObjectNode before, String methodName) {
    try {
      Map<UUID, View> beforeViews = before.getValueAsList(View.class, ViewContext.VIEWS)
          .stream()
          .collect(toMap(View::getUuid, v -> v));
      ObjectNode after = objectApi.create(SCHEMA, getCurrentViewContextEntry());
      List<ComponentModelChange> changes = after.getValueAsList(View.class, ViewContext.VIEWS)
          .stream()
          .filter(v -> beforeViews.containsKey(v.getUuid()))
          .filter(v -> ViewState.TO_CLOSE != v.getState())
          .map(v -> compareViewNodes(beforeViews.get(v.getUuid()), v))
          .filter(Objects::nonNull)
          .collect(toList());
      return new ViewContextChange()
          .viewContext(getCurrentViewContext())
          .changes(changes);
    } catch (Throwable tr) {
      throw new RuntimeException("Error after calling method " + methodName,
          tr);
    }
  }


  private ComponentModelChange compareViewNodes(View before, View after) {
    if (before != null) {
      before.getParameters().clear();
      before.getClosedChildrenViews().clear();
      before.getWidgetModels().clear();
      before.getCallbacks().clear();
    }
    if (after != null) {
      after.getParameters().clear();
      after.getClosedChildrenViews().clear();
      after.getWidgetModels().clear();
      after.getCallbacks().clear();
    }
    if (Objects.equals(before, after)) {
      return null;
    }
    return new ComponentModelChange()
        .uuid(after.getUuid())
        .path("/")
        .value(getComponentModel(after.getUuid()));
  }

  private Method getMethodForCode(Map<String, Map<String, Method>> methodsByView, String viewName,
      String code) {
    Map<String, Method> methods = methodsByView.get(viewName);
    if (methods == null) {
      return null;
    }
    Method method = methods.get(code);
    if (method == null) {
      // wildcard handler
      method = methods.get("");
    }
    return method;
  }

  private Method getMethodForWidgetAndCode(
      Map<String, Map<String, Map<String, Method>>> methodsByView,
      String viewName, String widget, String code) {
    Map<String, Map<String, Method>> methods = methodsByView.get(viewName);
    if (methods == null) {
      return null;
    }
    Map<String, Method> widgetMethods = methods.get(widget);
    if (widgetMethods == null) {
      // wildcard handler
      widgetMethods = methods.get("");
    }
    if (widgetMethods == null) {
      return null;
    }
    Method method = widgetMethods.get(code);
    if (method == null) {
      // wildcard handler
      method = widgetMethods.get("");
    }
    return method;
  }

  @Override
  public ViewContextChange performViewCall(ViewCall viewCall, String methodName) {
    ObjectNode before = beforeInvoke(methodName);
    try {
      viewCall.run();
    } catch (Throwable tr) {
      throw new RuntimeException("Error when calling method " + methodName,
          tr);
    }
    return afterInvoke(before, methodName);
  }

}
