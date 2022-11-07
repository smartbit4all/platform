package org.smartbit4all.api.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.bean.MessageResult;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
import org.smartbit4all.api.view.bean.ViewData;
import org.smartbit4all.api.view.bean.ViewStateUpdate;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;

public class ViewContextServiceImpl implements ViewContextService {

  private static final Logger log = LoggerFactory.getLogger(ViewContextServiceImpl.class);

  private static final ThreadLocal<UUID> currentViewContextUuid = new ThreadLocal<>();

  private static final String SCHEMA = "viewcontext";

  private Map<String, String> parentViewByViewName = new HashMap<>();

  private Map<String, Object> apiByViewName = new HashMap<>();

  private Map<String, Map<String, Method>> messageMethodsMap = new HashMap<>();

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
      }
      return storageInstance;
    }
  };

  @Override
  public ViewContext createViewContext() {
    UUID uuid = UUID.randomUUID();
    URI uri = storage.get().saveAsNew(
        new ViewContext()
            .uuid(uuid));
    sessionApi.addViewContext(uuid, uri);
    return readViewContext(uri);
  }

  @Override
  public void setCurrentViewContext(UUID uuid) {
    if (uuid == null) {
      currentViewContextUuid.remove();
    }
    currentViewContextUuid.set(uuid);
  }

  @Override
  public ViewContext getCurrentViewContext() {
    return readViewContext(getViewContextUri(currentViewContextUuid.get()));
  }

  @Override
  public ViewContext getViewContext(UUID uuid) {
    return readViewContext(getViewContextUri(uuid));
  }

  private ViewContext readViewContext(URI uri) {
    if (uri == null) {
      return null;
    }
    return storage.get().read(uri, ViewContext.class);
  }

  private URI getViewContextUri(UUID uuid) {
    Objects.requireNonNull(uuid, "ViewContext UUID must be not null");
    Map<String, URI> viewContexts = sessionApi.getViewContexts();
    URI uri = viewContexts.get(uuid.toString());
    if (uri == null) {
      throw new IllegalArgumentException("ViewContext not found by UUID");
    }
    return uri;
  }

  @Override
  public void updateCurrentViewContext(UnaryOperator<ViewContext> update) {
    storage.get().update(getViewContextUri(currentViewContextUuid.get()), ViewContext.class,
        update);
  }

  @Override
  public void updateViewContext(ViewContextUpdate updates) {
    storage.get().update(getViewContextUri(updates.getUuid()), ViewContext.class,
        c -> {
          updates.getUpdates().forEach(u -> updateViewState(c, u));
          return c;
        });
  }

  private void updateViewState(ViewContext context, ViewStateUpdate update) {
    context.getViews().stream()
        .filter(v -> update.getUuid().equals(v.getUuid()))
        .findFirst()
        .ifPresent(view -> view.setState(update.getState()));
  }

  @Override
  public String getParentViewName(String viewName) {
    String result = parentViewByViewName.get(viewName);
    return result == null ? "" : result;
  }

  @Override
  public ViewData getViewFromViewContext(UUID viewContextUuid, UUID viewUuid) {
    ViewContext viewContext;
    if (viewContextUuid == null) {
      viewContext = getCurrentViewContext();
    } else {
      viewContext = getViewContext(viewContextUuid);
    }
    Objects.requireNonNull(viewContext, "ViewContext must be not null!");
    return viewContext.getViews().stream()
        .filter(v -> viewUuid.equals(v.getUuid()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("View not found by UUID: " + viewUuid));
  }

  @Override
  public void handleMessage(UUID viewUuid, UUID messageUuid, MessageResult messageResult) {
    Objects.requireNonNull(messageResult, "MessageResult must be specified");
    Objects.requireNonNull(messageResult.getSelectedOption(),
        "MessageResult.selectedOption must be specified");
    Objects.requireNonNull(messageResult.getSelectedOption().getCode(),
        "MessageResult.selectedOption.code must be specified");

    ViewData view = getViewFromViewContext(null, viewUuid);
    Object api = apiByViewName.get(view.getViewName());
    Objects.requireNonNull(api, "API not found for view " + view.getViewName());
    Map<String, Method> messageMethods = messageMethodsMap.get(view.getViewName());
    if (messageMethods == null) {
      return;
    }

    String code = messageResult.getSelectedOption().getCode();
    Method messageHandlerMethod = messageMethods.get(code);
    if (messageHandlerMethod == null) {
      // wildcard handler
      messageHandlerMethod = messageMethods.get("");
    }
    if (messageHandlerMethod != null) {
      try {
        messageHandlerMethod.invoke(api, viewUuid, messageUuid, messageResult);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
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
    registerViewMessageMethods(viewName, api);
  }

  /**
   * Register all message handling methods of api for given view.
   * 
   * @param viewName
   * @param api
   */
  private void registerViewMessageMethods(String viewName, Object api) {
    Map<String, Method> messageMethods = new HashMap<>();
    ReflectionUtility.allMethods(
        api.getClass(),
        method -> method.isAnnotationPresent(MessageHandler.class))
        .forEach(method -> collectMessageMethod(viewName, method, messageMethods));

    messageMethodsMap.put(viewName, messageMethods);
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
}
