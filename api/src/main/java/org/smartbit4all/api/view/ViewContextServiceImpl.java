package org.smartbit4all.api.view;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.authentication.AuthenticationService;
import org.smartbit4all.api.cache.CacheService;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.exception.ExpiredSessionException;
import org.smartbit4all.api.session.exception.ViewContextMissigException;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.BeforeClose;
import org.smartbit4all.api.view.annotation.DataChangeListener;
import org.smartbit4all.api.view.annotation.MessageHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.CloseResult;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.ComponentModel;
import org.smartbit4all.api.view.bean.ComponentModelChange;
import org.smartbit4all.api.view.bean.DataChange;
import org.smartbit4all.api.view.bean.DataChangeEvent;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.MessageResult;
import org.smartbit4all.api.view.bean.OpenPendingData;
import org.smartbit4all.api.view.bean.ServerRequestExecutionStat;
import org.smartbit4all.api.view.bean.ServerRequestTrack;
import org.smartbit4all.api.view.bean.ServerRequestType;
import org.smartbit4all.api.view.bean.StatisticRecord;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextChange;
import org.smartbit4all.api.view.bean.ViewContextData;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
import org.smartbit4all.api.view.bean.ViewData;
import org.smartbit4all.api.view.bean.ViewEventHandler;
import org.smartbit4all.api.view.bean.ViewEventHandler.ViewEventTypeEnum;
import org.smartbit4all.api.view.bean.ViewPlaceholder;
import org.smartbit4all.api.view.bean.ViewState;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectSerializer;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StoragePerformanceRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;

public class ViewContextServiceImpl implements ViewContextService {

  public interface ViewCall {
    Object run() throws RuntimeException;
  }

  private static class ViewComparisonResult {
    ComponentModelChange change;
    Map<String, Object> oldValues;
    Map<String, Object> newValues;
  }

  private static final class PerformActionEventContext {
    final View view;
    final UiActionRequest request;
    String widgetId;
    String nodeId;
    Map<String, Object> viewContextBefore;
    Map<String, Object> viewContextAfter;

    private PerformActionEventContext(View view, UiActionRequest request) {
      this.view = view;
      this.request = request;
    }

  }

  private static final Logger log = LoggerFactory.getLogger(ViewContextServiceImpl.class);

  private static final ThreadLocal<ViewContext> currentViewContext = new ThreadLocal<>();

  private static final ThreadLocal<Map<UUID, View>> currentLoadedPlaceholders = new ThreadLocal<>();

  private static final ThreadLocal<ServerRequestTrack> currentServerRequestTrack =
      new ThreadLocal<>();

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
   * <View.name <DataChangeListener.value <Method>>>
   *
   */
  private Map<String, Map<String, Method>> dataChangeListenerMethodsByView = new HashMap<>();

  /**
   * <View.name <WidgetActionHandler.widget <WidgetActionHandler.value <Method>>>>
   *
   */
  private Map<String, Map<String, Map<String, Method>>> widgetActionMethodsByViewAndWidget =
      new HashMap<>();

  private Map<String, Method> beforeCloseMethodsByView = new HashMap<>();

  @Value("${session.refresh-timeout-min:120}")
  private int refreshTimeoutMins;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private ViewPublisherApi publisherApi;

  @Autowired
  private CacheService cacheService;

  public static boolean collectExecution = false;

  /**
   * The execution statistic of the running server.
   */
  private static final Map<String, ServerRequestExecutionStat> executionStat = new HashMap<>();

  private static ReadWriteLock rwlExecutionStat = new ReentrantReadWriteLock();

  private List<String> requestCodesToSkipWithMissingView =
      new ArrayList<>(defaultRequestCodesToSkipWithMissingView);
  private static final List<String> defaultRequestCodesToSkipWithMissingView =
      Arrays.asList(PageApi.DEFAULT_CLOSE);

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
            .collect(toList()))
        .links(context.getLinks())
        .downloads(context.getDownloads())
        .clipboardData(context.getClipboardData());
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
      throw new MissinCurrentViewContextException(
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
    startServerRequest(new ServerRequestTrack().type(ServerRequestType.GET_VIEW_CONTEXT));
    UUID currentUuid = getCurrentViewContextUuid();
    if (!Objects.equals(uuid, currentUuid)) {
      throw new IllegalArgumentException("currentViewContext doesn't match paramater");
    }
    finishServerRequest();
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
    startServerRequest(new ServerRequestTrack().type(ServerRequestType.UPDATE_VIEW_CONTEXT));
    if (updates.getUpdates() != null && !updates.getUpdates().stream().allMatch(
        update -> update.getState() == ViewState.OPENED
            || update.getState() == ViewState.CLOSED)) {
      throw new IllegalArgumentException("Only OPENED and CLOSED updates allowed");
    }
    updateCurrentViewContext(
        c -> {
          processUpdates(c, updates);
          processDeviceInfo(c, updates);
          return c;
        });
    finishServerRequest();
  }

  private void processUpdates(ViewContext c, ViewContextUpdate updates) {
    if (updates.getUpdates() != null) {
      updates.getUpdates().forEach(u -> ViewContexts.updateViewState(c, u));
      c.getViews().removeIf(v -> ViewState.CLOSED == v.getState());
    }
  }

  private void fireActionExecuted(PerformActionEventContext ctx) {
    publisherApi.fireActionExecuted(
        ctx.view, ctx.request, ctx.widgetId,
        ctx.nodeId, ctx.viewContextBefore, ctx.viewContextAfter);
  }

  private void processDeviceInfo(ViewContext c, ViewContextUpdate updates) {
    if (updates.getDeviceInfo() != null
        && !Objects.equals(updates.getDeviceInfo(), c.getDeviceInfo())) {
      c.setDeviceInfo(updates.getDeviceInfo());
      publisherApi.fireDeviceInfoChanged(c.getUuid(), updates.getDeviceInfo());
    }
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
    return getView(viewContext, viewUuid);
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
        if (modelWasEmpty) {
          publisherApi.fireViewOpened(view,
              view.getObjectUri() != null ? view.getObjectUri().toString() : null,
              view.getViewName());
        }
        return null;
      }
      modelObject = ((PageApi<?>) api).initModel(view);
      view.setModel(modelObject);
    }
    if (clazz.isInstance(modelObject)) {
      if (modelWasEmpty) {
        publisherApi.fireViewOpened(view,
            view.getObjectUri() != null ? view.getObjectUri().toString() : null,
            view.getViewName());
        view.putParametersItem(ViewContexts.INITIAL_MODEL, modelObject);
      }
      return (M) modelObject;
    }
    M model = objectApi.asType(clazz, view.getModel());
    // this is to ensure View holds a typed object, not a Map representing the object
    view.setModel(model);
    if (modelWasEmpty) {
      publisherApi.fireViewOpened(view,
          view.getObjectUri() != null ? view.getObjectUri().toString() : null, view.getViewName());
      view.putParametersItem(ViewContexts.INITIAL_MODEL, modelObject);
    }
    return model;
  }

  @Override
  public ViewContextChange handleMessage(UUID viewUuid, UUID messageUuid,
      MessageResult messageResult) {
    return performViewCall(() -> handleMessageInternal(viewUuid, messageUuid, messageResult),
        "handleMessage");
  }

  private Object handleMessageInternal(UUID viewUuid, UUID messageUuid,
      MessageResult messageResult) {
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
      return null;
    }
    View view;
    try {
      view = getViewFromCurrentViewContext(viewUuid);
    } catch (Exception e) {
      // view not found -> log error and remove message to avoid infinite loop
      log.error("Unexpected error when retreiving message's view", e);
      updateCurrentViewContext(
          c -> ViewContexts.updateViewState(c, messageUuid, ViewState.TO_CLOSE));
      return null;
    }
    Object api = apiByViewName.get(view.getViewName());
    Objects.requireNonNull(api, "API not found for view " + view.getViewName());
    Method method = getMethodForCode(messageMethodsByView, view.getViewName(),
        messageResult.getSelectedOption().getCode());
    startServerRequest(new ServerRequestTrack().type(ServerRequestType.HANDLE_MESSAGE_RESULT)
        .viewUuid(viewUuid).viewName(view.getViewName()));

    if (method != null) {
      try {
        // TODO examine signature, try to support many variations
        method.invoke(api, viewUuid, messageUuid, messageResult);
      } catch (IllegalAccessException | IllegalArgumentException e) {
        log.error("Error when calling MessageHandler method " + method.getName(), e);
      } catch (InvocationTargetException e) {
        if (e.getCause() instanceof RuntimeException) {
          throw (RuntimeException) e.getCause();
        }
        log.error(
            "InvocationTargetException when calling MessageHandler method " + method.getName(),
            e.getCause());
      }
    }
    updateCurrentViewContext(
        c -> ViewContexts.updateViewState(c, messageUuid, ViewState.TO_CLOSE));
    finishServerRequest();
    return null;
  }

  @EventListener(ContextRefreshedEvent.class)
  protected void initViews(ContextRefreshedEvent applicationPreparedEvent) {
    apiByViewName.clear();
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
    registerAnnotatedMethodsWithCode(viewName, api,
        MessageHandler.class, messageMethodsByView,
        annotation -> Arrays.asList(annotation.value()));
    registerAnnotatedMethodsWithCode(viewName, api,
        ActionHandler.class, actionMethodsByView,
        annotation -> Arrays.asList(annotation.value()));
    registerAnnotatedMethodsWithCode(viewName, api,
        DataChangeListener.class, dataChangeListenerMethodsByView,
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
  public void execute(UUID uuid, ViewContextCommand command, boolean readOnly, boolean userAction)
      throws Exception {
    Objects.requireNonNull(uuid, "currentViewContextUuid is not set");
    Objects.requireNonNull(command, "command is not set");
    URI viewContextUri = sessionApi.getViewContexts().get(uuid.toString());
    if (viewContextUri == null) {
      throw new ViewContextMissigException();
    }
    Lock lock = null;
    if (!readOnly) {
      lock = objectApi.getLock(viewContextUri);
      lock.lock();
    }
    try {
      ObjectNode contextNode = objectApi.load(viewContextUri);
      // clear links & downloads on load
      contextNode.modify(ViewContext.class,
          c -> {
            if (c.getTimeOfLastRequest() != null
                && Duration.between(c.getTimeOfLastRequest(), OffsetDateTime.now())
                    .toSeconds() > refreshTimeoutMins * 60) {
              sessionManagementApi.updateSession(sessionApi.getSessionUri(),
                  s -> s.expiration(null).refreshExpiration(null));
              authenticationService.logout();
              throw new ExpiredSessionException();
            }

            if (userAction) {
              c.timeOfLastRequest(OffsetDateTime.now());
            }

            if (c.getLinks() != null) {
              c.getLinks().clear();
            }
            if (c.getDownloads() != null) {
              c.getDownloads().clear();
            }
            if (c.getClipboardData() != null) {
              c.getClipboardData().clear();
            }
            return c;
          });
      currentViewContext.set(contextNode.getObject(ViewContext.class));
      currentLoadedPlaceholders.set(new HashMap<>());
      cacheService.startRequestScope();
      command.execute();
      if (!readOnly) {
        currentLoadedPlaceholders.get().forEach((viewUuid, view) -> {
          saveView(view);
        });
        contextNode.modify(ViewContext.class, c -> currentViewContext.get());
        objectApi.save(contextNode);
      }
    } finally {
      currentLoadedPlaceholders.remove();
      currentViewContext.remove();
      cacheService.endRequestScope();
      if (lock != null) {
        lock.unlock();
      }
    }
  }

  @Override
  public ComponentModel getComponentModel(UUID viewUuid) {
    View view = getViewFromCurrentViewContext(viewUuid);
    startServerRequest(new ServerRequestTrack().type(ServerRequestType.GET_COMPONENT_MODEL)
        .viewUuid(viewUuid).viewName(view.getViewName()));
    Objects.requireNonNull(view, "View not found!");
    Object data = getModel(viewUuid, null);
    ComponentModel componentModel = createComponentModel(view, data);
    finishServerRequest();
    return componentModel;
  }

  private ComponentModel createComponentModel(View view, Object data) {
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
        .layouts(view.getLayouts())
        .componentLayouts(view.getComponentLayouts())
        .widgets(new ArrayList<>(view.getWidgetModels().keySet()))
        .style(view.getStyle())
        .parentStyle(view.getParentStyle());
  }

  @Override
  public ViewContextChange getComponentModel2(UUID viewUuid) {
    ViewContextChange result = performViewCall(
        () -> getComponentModel(viewUuid), "getComponentModel2");
    ComponentModelChange change = result.getChanges().stream()
        .filter(ch -> viewUuid.equals(ch.getUuid()))
        .findFirst().orElse(null);
    if (change == null) {
      change = new ComponentModelChange().uuid(viewUuid);
      result.getChanges().add(change);
    }
    if (change.getValue() == null) {
      // view hasn't changed during initModel / load, maybe initialized before
      change
          .path(StringConstant.EMPTY)
          .value(getComponentModel(viewUuid));
    }
    // make sure this view is in 'whole'
    Map<String, Object> changes = new HashMap<>();
    // this is the whole model at '' path
    changes.put(change.getPath(), change.getValue());
    change.setChanges(changes);
    return result;
  }

  @Override
  public ViewContextChange performAction(UUID viewUuid, UiActionRequest request) {
    Objects.requireNonNull(request, "Request must be specified!");
    Objects.requireNonNull(request.getCode(), "Request.code must be specified!");
    View view = null;
    try {
      view = getViewFromCurrentViewContext(viewUuid);
    } catch (Exception e) {
      // TODO: handle VIEW_NOT_FOUND_BY_UUID with specific Exception
      if (e.getMessage() != null && e.getMessage().startsWith(ViewContexts.VIEW_NOT_FOUND_BY_UUID)
          && requestCodesToSkipWithMissingView.contains(request.getCode())) {
        return createViewContextChange(Collections.emptyList(), null);
      } else {
        throw e;
      }
    }
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

    final PerformActionEventContext ctx = new PerformActionEventContext(view, request);
    startServerRequest(new ServerRequestTrack().type(ServerRequestType.ACTION).request(request)
        .viewUuid(viewUuid).viewName(view.getViewName()));
    List<ViewComparisonResult> comparisons =
        invokeMethodInternal(ctx, eventDescriptor, method, api, viewUuid, request);
    finishServerRequest();
    return createViewContextChange(comparisons, null); // ActionHandler is void
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
    if (request.getParams().containsKey(UiActions.CLIENT_PAGE_MODEL)) {
      eventDescriptor.getBeforeEvents().add(
          new ViewEventHandler()
              .viewEventType(ViewEventTypeEnum.BEFORE)
              .addPathItem(ViewEventApi.WIDGET)
              .addPathItem(request.getCode())
              .invocationRequest(invocationApi.builder(ViewContextService.class)
                  .build(service -> service.setClientPageModelFromRequest(
                      viewUuid, nodeId, widgetId, request))));
    }
    final PerformActionEventContext ctx = new PerformActionEventContext(view, request);
    ctx.widgetId = widgetId;
    ctx.nodeId = nodeId;
    startServerRequest(
        new ServerRequestTrack().type(ServerRequestType.WIDGET_ACTION).request(request)
            .viewUuid(viewUuid).viewName(view.getViewName()).widgetId(widgetId).nodeId(nodeId));
    List<ViewComparisonResult> comparisons =
        invokeMethodInternal(ctx, eventDescriptor, method, api, viewUuid, widgetId,
            nodeId, request);
    finishServerRequest();
    return createViewContextChange(comparisons, null); // WidgetActionHandler is void
  }

  @Override
  public void setClientPageModelFromRequest(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    if (request.getParams().containsKey(UiActions.CLIENT_PAGE_MODEL)) {
      Object modelObject = request.getParams().get(UiActions.CLIENT_PAGE_MODEL);
      if (modelObject instanceof Map<?, ?>) {
        Object data = ((Map) modelObject).get(ComponentModel.DATA);
        getViewFromCurrentViewContext(viewUuid).setModel(data);
      }
    }
  }

  /**
   * @param method Runnable is used here as a void -> void functional interface, this is the method
   *        invocation.
   * @return
   */
  private List<ViewComparisonResult> invokeMethodInternal(PerformActionEventContext ctx,
      ViewEventDescriptor eventDescriptor,
      Method method,
      Object api, Object... args) {
    InvocationRequest insteadOfRequest = null;
    if (eventDescriptor.getInsteadOf() != null) {
      insteadOfRequest = eventDescriptor.getInsteadOf().getInvocationRequest();
    }

    if (insteadOfRequest == null && method == null) {
      // this is handled before calling this method, but we're playing safe here
      throw new IllegalStateException("Neither insteadOfRequest or method found!");
    }

    ObjectNode before = beforeInvoke(getMethodName(insteadOfRequest, method));
    ctx.viewContextBefore = before.getObjectAsMap();
    InvocationRequest invocationRequest = null;
    try {
      // The before events can block the execution of the whole action. If an invocation throws
      // exception then the execution is interrupted. If we would like to continue the execution of
      // TODO the before events then we have to throw some special exception or so.
      for (ViewEventHandler eventHandler : eventDescriptor.getBeforeEvents()) {
        invocationRequest = eventHandler.getInvocationRequest();
        invocationApi.invoke(invocationRequest, args);
      }

      if (insteadOfRequest == null) {
        method.invoke(api, args);
      } else {
        invocationRequest = insteadOfRequest;
        invocationApi.invoke(invocationRequest, args);
      }

      // These event won't block the execution. Their exceptions and error are logged but the
      // execution continues.
      for (ViewEventHandler eventHandler : eventDescriptor.getAfterEvents()) {
        invocationRequest = eventHandler.getInvocationRequest();
        try {
          invocationApi.invoke(invocationRequest, args);
        } catch (Exception e) {
          log.error("Error in afterEventHandler " + invocationRequest, e);
        }
      }
    } catch (ApiNotFoundException e) {
      throw new RuntimeException(
          "ApiNotFoundException when calling method " + getMethodName(invocationRequest, method),
          e);
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
      if (e.getCause() != null && e.getCause().getMessage() != null) {
        throw new RuntimeException(
            e.getCause().getMessage(),
            e);
      }
      throw new RuntimeException(
          "InvocationTargetException without cause calling method "
              + getMethodName(invocationRequest, method),
          e);
    }
    return afterInvoke(ctx, before, getMethodName(invocationRequest, method));
  }

  private final String getMethodName(InvocationRequest request, Method method) {
    if (request != null) {
      return request.toString();
    }
    return method != null ? method.getName() : "unknown";
  }

  private ObjectNode beforeInvoke(String methodName) {
    try {
      return objectApi.create(SCHEMA, getCurrentViewContextEntry());
    } catch (Throwable tr) {
      throw new RuntimeException("Error before calling method " + methodName,
          tr);
    }
  }

  private List<ViewComparisonResult> afterInvoke(PerformActionEventContext ctx, ObjectNode before,
      String methodName) {
    try {
      Map<UUID, View> beforeViews = before.getValueAsList(View.class, ViewContext.VIEWS)
          .stream()
          .collect(toMap(View::getUuid, v -> v));
      ObjectNode after = objectApi.create(SCHEMA, getCurrentViewContextEntry());
      if (ctx != null) {
        ctx.viewContextAfter = after.getObjectAsMap();
        fireActionExecuted(ctx);
      }

      return after.getValueAsList(View.class, ViewContext.VIEWS)
          .stream()
          .filter(v -> beforeViews.containsKey(v.getUuid()))
          .filter(v -> ViewState.TO_CLOSE != v.getState()
              && ViewState.OPEN_PENDING != v.getState())
          .map(v -> compareViewNodes(beforeViews.get(v.getUuid()), v))
          .filter(Objects::nonNull)
          .collect(toList());
    } catch (Throwable tr) {
      throw new RuntimeException("Error after calling method " + methodName,
          tr);
    }
  }

  private ViewContextChange createViewContextChange(List<ViewComparisonResult> comparisons,
      Object result) {
    return new ViewContextChange()
        .viewContext(getCurrentViewContext())
        .changes(comparisons.stream()
            .map(comp -> comp.change)
            .collect(toList()))
        .result(result);
  }

  private ViewComparisonResult compareViewNodes(View before, View after) {
    Objects.requireNonNull(after, "After view must not be null");
    if (before != null) {
      before.getParameters().clear();
      before.getVariables().clear();
      before.getClosedChildrenViews().clear();
      before.getCallbacks().clear();
      before.getDownloadableItems().clear();
      before.getEventHandlers().clear();
    }
    after.getParameters().clear();
    after.getVariables().clear();
    after.getClosedChildrenViews().clear();
    after.getCallbacks().clear();
    after.getDownloadableItems().clear();
    after.getEventHandlers().clear();
    if (Objects.equals(before, after)) {
      return null;
    }
    ViewComparisonResult result = new ViewComparisonResult();
    ComponentModel afterComponentModel;
    try {
      afterComponentModel = getComponentModel(after.getUuid());
    } catch (IllegalArgumentException e) {
      // not PageApi, model is not available
      afterComponentModel = createComponentModel(after, after.getModel());
    }
    result.change = new ComponentModelChange()
        .uuid(after.getUuid())
        .path(StringConstant.EMPTY)
        .value(afterComponentModel);
    try {
      // handle model (data)

      if (before != null && before.getModel() != null) {

        // TODO compare data doesn't work well for complex objects, for now we'll send everything.
        // // ****** check for individual changes ******
        // // before is from an objectNode, model is Map
        // Map<String, Object> beforeModel = (Map<String, Object>) before.getModel();
        // Map<String, Object> afterModel = (Map<String, Object>) after.getModel();
        // ObjectChangeData changes = compareApi.changesOfMap(beforeModel, afterModel);
        // result.oldValues = compareApi.toMap(changes, ComponentModel.DATA, false);
        // result.newValues = compareApi.toMap(changes, ComponentModel.DATA, true);
        // result.change.changes(result.newValues);
        // // ****** check for individual changes ******

        // ****** send whole componentModel ******
        result.change.changes(new HashMap<>());
        result.change.getChanges().put(StringConstant.EMPTY, afterComponentModel);
        // we should still fill oldValues/newValues so any DataChange aware method can use it
        result.oldValues = new HashMap<>();
        result.oldValues.put(ComponentModel.DATA, null);
        result.newValues = new HashMap<>();
        result.newValues.put(ComponentModel.DATA, after.getModel());
        // ****** send whole componentModel ******
        // // valuesets
        // result.change.getChanges().putAll(
        // findDifferences(ComponentModel.VALUE_SETS,
        // before.getValueSets(),
        // after.getValueSets()));
        // // layouts
        // result.change.getChanges().putAll(
        // findDifferences(ComponentModel.LAYOUTS,
        // before.getLayouts(),
        // after.getLayouts()));
        // // layouts
        // result.change.getChanges().putAll(
        // findDifferences(ComponentModel.COMPONENT_LAYOUTS,
        // before.getComponentLayouts(),
        // after.getComponentLayouts()));
        // // actions
        // if (!Objects.deepEquals(after.getActions(), before.getActions())) {
        // result.change.getChanges().put(ComponentModel.ACTIONS, after.getActions());
        // }
        // // constraints
        // List<ComponentConstraint> beforeConstraints =
        // before.getConstraint() == null ? Collections.emptyList()
        // : before.getConstraint().getComponentConstraints();
        // List<ComponentConstraint> afterConstraints =
        // after.getConstraint() == null ? Collections.emptyList()
        // : after.getConstraint().getComponentConstraints();
        // if (!Objects.deepEquals(beforeConstraints, afterConstraints)) {
        // result.change.getChanges().put(ComponentModel.CONSTRAINTS, afterConstraints);
        // }
        // widgets - full componentModel change doesn't refresh widgets, send them anyway
        result.change.changedWidgets(new ArrayList<>(
            findDifferences(null,
                before.getWidgetModels(),
                after.getWidgetModels())
                    .keySet()));

      } else {
        // whole model is new, we can use ComponentModelChange.path/value for now to avoid double
        // ComponentModel creation

        result.change.changes(new HashMap<>());
        result.change.getChanges().put(result.change.getPath(), result.change.getValue());
        // we should still fill oldValues/newValues so any DataChange aware method can use it
        result.oldValues = new HashMap<>();
        result.oldValues.put(ComponentModel.DATA, null);
        result.newValues = new HashMap<>();
        result.newValues.put(ComponentModel.DATA, after.getModel());
      }
    } catch (Exception e) {
      log.error("Unexpected error when calculating changes", e);
    }
    return result;
  }

  private Map<String, Object> findDifferences(String path, Map<String, Object> before,
      Map<String, Object> after) {
    // add deleted entries to after
    before.entrySet().stream()
        .filter(e -> !after.containsKey(e.getKey()))
        .forEach(e -> after.put(e.getKey(), null));
    // compare existing entries
    String pathPrefix = Strings.isNullOrEmpty(path) ? "" : path + StringConstant.DOT;
    before.replaceAll((k, v) -> v == null ? new HashMap<String, Object>() : v);
    after.replaceAll((k, v) -> v == null ? new HashMap<String, Object>() : v);
    return after.entrySet().stream()
        .filter(e -> !Objects.equals(e.getValue(), before.get(e.getKey())))
        .collect(toMap(
            e -> pathPrefix + e.getKey(),
            Entry::getValue));
  }

  // TODO use it.. missing: source of DataChangeEvent
  private ComponentModelChange notifyDataChangeListeners(ViewComparisonResult viewComparison) {
    try {
      UUID viewUuid = viewComparison.change.getUuid();
      View view = getViewFromCurrentViewContext(viewUuid);
      Object api = apiByViewName.get(view.getViewName());
      Objects.requireNonNull(api, "API not found for view " + view.getViewName());
      for (Entry<String, Object> change : viewComparison.change.getChanges().entrySet()) {
        Method method =
            getMethodForCode(dataChangeListenerMethodsByView, view.getViewName(), change.getKey());
        if (method != null) {
          try {
            method.invoke(api, viewUuid,
                new DataChangeEvent()
                    .newValues(viewComparison.newValues)
                    .oldValues(viewComparison.oldValues));
          } catch (IllegalAccessException | IllegalArgumentException
              | InvocationTargetException e) {
            log.error("Error when calling MessageHandler method " + method.getName(), e);
          }
        }

      }
    } catch (Throwable tr) {
      log.error("Unexpected error when handling modelChange " + viewComparison.change, tr);
    }
    return viewComparison.change;
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
    if (method == null) {
      // go back to wildcard grid, and try from there
      widgetMethods = methods.get("");
      if (widgetMethods != null) {
        method = widgetMethods.get(code);
        if (method == null) {
          // wildcard handler
          method = widgetMethods.get("");
        }
      }
    }
    return method;
  }

  @Override
  public ViewContextChange performViewCall(ViewCall viewCall, String methodName) {
    ObjectNode before = beforeInvoke(methodName);
    Object result;
    try {
      result = viewCall.run();
    } catch (Throwable tr) {
      log.error("Error when calling method " + methodName, tr);
      throw tr;
    }
    List<ViewComparisonResult> comparisons = afterInvoke(null, before, methodName);
    return createViewContextChange(comparisons, result);
  }

  @Override
  public ViewContextChange performDataChanged(UUID viewUuid, DataChange event) {
    // get current model as map
    View view = getViewFromCurrentViewContext(viewUuid);
    Objects.requireNonNull(view, "View not found when performing data change!");
    Class<?> clazz = modelClassByViewName.get(view.getViewName());
    if (clazz == null) {
      throw new IllegalArgumentException(
          "View is not PageApi and model clazz is not specified! " + view.getViewName());
    }
    Object modelBeforeChange = view.getModel();
    Objects.requireNonNull(modelBeforeChange, "Model is not set when performing data change!");
    ObjectDefinition<?> definition = objectApi.definition(clazz);
    if (clazz.isInstance(modelBeforeChange)) {
      log.warn("Possible model reference override when performing data change in view {}",
          view.getViewName());
    } else if (!(modelBeforeChange instanceof Map)) {
      log.warn("Suspicous object in model (type={})when performing data change in view {}",
          modelBeforeChange.getClass().getName(), view.getViewName());
    }
    if (!(modelBeforeChange instanceof Map)) {
      modelBeforeChange = definition.toMap(modelBeforeChange);
    }

    // perform data change on map, set as view's model
    ObjectNode modelNode =
        objectApi.create(SCHEMA, definition, (Map<String, Object>) modelBeforeChange);
    if (event.getValues().size() == 1 && event.getValues().containsKey("")) {
      modelNode.setValues((Map<String, Object>) event.getValues().get(""));
    } else {
      event.getValues().forEach((key, value) -> modelNode.setValue(value, key.split("\\.")));
    }
    view.setModel(modelNode.getObjectAsMap());

    // notify data listeners, calculate changes during data change processing and return
    return performViewCall(() -> {
      return null;
    }, "performDataChanged");
  }

  /**
   * Adds a ui action code, so the incoming request with this code will not fail even when the given
   * view by the performAcion's viewUuid parameter is missing.
   */
  public void addRequestCodeToSkipWithMissingView(String actionCode) {
    Objects.requireNonNull(actionCode, "actionCode can not be null!");
    requestCodesToSkipWithMissingView.add(actionCode);
  }

  @Override
  public void startServerRequest(ServerRequestTrack serverRequest) {
    serverRequest.startTime(OffsetDateTime.now());
    ViewContext viewContext = currentViewContext.get();
    if (viewContext != null) {
      viewContext.currentRequest(serverRequest);
    } else {
      currentServerRequestTrack.set(serverRequest);
    }
    StorageFS.startRequest();
  }

  @Override
  public void finishServerRequest() {
    ServerRequestTrack serverRequest = getServerRequest();
    if (serverRequest == null) {
      return;
    }
    OffsetDateTime now = OffsetDateTime.now();
    serverRequest.endTime(now);
    currentServerRequestTrack.remove();
    if (!collectExecution) {
      return;
    }
    long executionTime = serverRequest.getEndTime().toInstant().toEpochMilli()
        - serverRequest.getStartTime().toInstant().toEpochMilli();
    rwlExecutionStat.writeLock().lock();
    try {
      String serverRequestId = getServerRequestId(serverRequest);
      ServerRequestExecutionStat requestExecutionStat =
          executionStat.computeIfAbsent(serverRequestId,
              s -> new ServerRequestExecutionStat().id(s).viewName(serverRequest.getViewName())
                  .widgetId(serverRequest.getWidgetId())
                  .actionCode(
                      serverRequest.getRequest() != null ? serverRequest.getRequest().getCode()
                          : StringConstant.UNKNOWN)
                  .fullStat(new StatisticRecord())
                  .writeCount(new StatisticRecord()).writeStat(new StatisticRecord())
                  .readCount(new StatisticRecord()).readStat(new StatisticRecord())
                  .type(serverRequest.getType()));
      updateStat(executionTime, requestExecutionStat.getFullStat());
      StoragePerformanceRecord storagePerformanceRecord = StorageFS.finishRequest();
      if (storagePerformanceRecord != null) {
        updateStat(storagePerformanceRecord.getReadNumber(), requestExecutionStat.getReadCount());
        updateStat(storagePerformanceRecord.getReadTime(), requestExecutionStat.getReadStat());
        updateStat(storagePerformanceRecord.getWriteNumber(), requestExecutionStat.getWriteCount());
        updateStat(storagePerformanceRecord.getWriteTime(), requestExecutionStat.getWriteStat());
      }
    } catch (Exception e) {
      log.error("Unable to update execution statistic.", e);
    } finally {
      rwlExecutionStat.writeLock().unlock();
    }
  }

  private final void updateStat(long value, StatisticRecord stat) {
    stat
        .setCounter(stat.getCounter() + 1);
    stat
        .sum(stat.getSum() + value);
    stat
        .setAvg(stat.getSum()
            / stat.getCounter());
    if (stat.getMin() == null
        || value < stat.getMin()) {
      stat.min(value);
    }
    if (stat.getMax() == null
        || value > stat.getMax()) {
      stat.max(value);
    }
  }

  @Override
  public String getExecutionStatJSON() {
    ObjectSerializer serializer =
        objectApi.definition(ServerRequestExecutionStat.class).getDefaultSerializer();
    rwlExecutionStat.readLock().lock();
    try {
      return StringConstant.LEFT_SQUARE + executionStat.values().stream()
          .map(s -> {
            try {
              return serializer.writeValueAsString(s);
            } catch (JsonProcessingException e) {
              return null;
            }
          }).filter(Objects::nonNull).collect(joining(StringConstant.COMMA_SPACE))
          + StringConstant.RIGHT_SQUARE;
    } finally {
      rwlExecutionStat.readLock().unlock();
    }
  }

  private final String getServerRequestId(ServerRequestTrack serverRequest) {
    return (Strings.isNullOrEmpty(serverRequest.getViewName()) ? "ViewContext"
        : serverRequest.getViewName())
        + (Strings.isNullOrEmpty(serverRequest.getWidgetId()) ? StringConstant.EMPTY
            : StringConstant.SPACE_HYPHEN_SPACE + serverRequest.getWidgetId())
        + ((serverRequest.getRequest() == null || serverRequest.getRequest().getCode() == null)
            ? StringConstant.EMPTY
            : StringConstant.SPACE_HYPHEN_SPACE + serverRequest.getRequest().getCode());
  }

  @Override
  public ServerRequestTrack getServerRequest() {
    ViewContext viewContext = currentViewContext.get();
    if (viewContext != null) {
      return viewContext.getCurrentRequest();
    }
    return currentServerRequestTrack.get();
  }

  @Override
  public Object getApiByViewName(String viewName) {
    return apiByViewName.get(viewName);
  }

  @Override
  public ViewPlaceholder createViewPlaceholder(View view) {
    currentLoadedPlaceholders.get().put(view.getUuid(), view);
    return new ViewPlaceholder()
        .uuid(view.getUuid())
        .closedChildrenViews(view.getClosedChildrenViews());
  }

  @Override
  public View getViewFromPlaceholder(ViewPlaceholder placeholder) {
    View view = currentLoadedPlaceholders.get().get(placeholder.getUuid());
    if (view == null) {
      view = getPlaceholderReference(placeholder.getUuid()).get();
      currentLoadedPlaceholders.get().put(placeholder.getUuid(), view);
    }
    return view;
  }

  @Override
  public View getAndClearViewFromPlaceholder(ViewPlaceholder placeholder) {
    View view = getViewFromPlaceholder(placeholder);
    currentLoadedPlaceholders.get().remove(placeholder.getUuid());
    return view;
  }

  protected void saveView(View view) {
    getPlaceholderReference(view.getUuid()).update(v -> view);
  }

  private StoredReference<View> getPlaceholderReference(UUID viewUuid) {
    return collectionApi.reference(getCurrentViewContextEntry().getUri(),
        SCHEMA_PLACEHOLDERS,
        viewUuid.toString(),
        View.class);
  }

  @Override
  public View getView(ViewContext context, UUID viewUuid) {
    View view = context.getViews().stream()
        .filter(v -> viewUuid.equals(v.getUuid()))
        .findFirst()
        .orElse(null);
    if (view == null) {
      // check closed children views, where model is kept
      List<ViewPlaceholder> closedViews = context.getViews().stream()
          .map(View::getClosedChildrenViews)
          .flatMap(List::stream)
          .collect(toList());
      ViewPlaceholder placeholder = getViewsIncludingClosedChildren(closedViews)
          .filter(v -> viewUuid.equals(v.getUuid()))
          // .filter(v -> v != null && v.getModel() != null)
          .findFirst()
          .orElseThrow(
              () -> new IllegalArgumentException(ViewContexts.VIEW_NOT_FOUND_BY_UUID + viewUuid));
      view = getViewFromPlaceholder(placeholder);
      if (view == null || view.getModel() == null) {
        new IllegalArgumentException(ViewContexts.VIEW_NOT_FOUND_BY_UUID + viewUuid);
      }
    }
    return view;
  }

  private static Stream<ViewPlaceholder> getViewsIncludingClosedChildren(
      List<ViewPlaceholder> views) {
    return Stream.concat(
        views.stream(),
        views.stream()
            .flatMap(v -> getViewsIncludingClosedChildren(v.getClosedChildrenViews())));
  }

  public static class MissinCurrentViewContextException extends IllegalStateException {
    public MissinCurrentViewContextException(String msg) {
      super(msg);
    }
  }

  @Override
  public Map<String, Object> getCache(UUID viewUuid) {
    Objects.requireNonNull(viewUuid, "viewUuid must be specified");
    return cacheService.getRequestScopedCache(viewUuid.toString());
  }

}
