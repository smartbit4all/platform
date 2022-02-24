package org.smartbit4all.ui.api.viewmodel;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.ViewModelData;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.disposables.Disposable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ViewModelImpl<T> extends ObjectEditingImpl implements ViewModel {

  private static final Logger log = LoggerFactory.getLogger(ViewModelImpl.class);

  protected ViewModelImpl<?> parent;
  protected String path;

  protected ObservableObjectImpl data;

  protected T model;

  private Class<T> modelClazz;

  protected Consumer<URI> listener;

  private Disposable subscription;

  private Map<Class<?>, ApiBeanDescriptor> apiBeanDescriptors;

  /**
   * First key: commandCode, embedded map key: commandPath (may be null)
   */
  protected Map<String, Map<String, WrapperCommand<?>>> commandsByCode = new HashMap<>();

  protected Map<String, ViewModel> childrenByPath = new HashMap<>();

  protected ViewModelImpl(ObservablePublisherWrapper publisherWrapper,
      Map<Class<?>, ApiBeanDescriptor> apiBeanDescriptors,
      Class<T> modelClazz) {
    super();
    data = new ObservableObjectImpl(publisherWrapper);
    this.apiBeanDescriptors = apiBeanDescriptors;
    this.modelClazz = modelClazz;
  }

  @Override
  public void addChild(ViewModel child, String path) {
    // ???
    child = ReflectionUtility.getProxyTarget(child);
    if (child instanceof ViewModelImpl) {
      ViewModelImpl<?> childVM = (ViewModelImpl<?>) child;
      childVM.initByParentRef(this, path);
      childrenByPath.put(path, childVM);
    }
  }

  /**
   * Register commands here.
   */
  protected abstract void initCommands();

  /**
   * Load model by parameters in navigationTarget.
   * 
   * @param navigationTarget
   * @return
   */
  protected abstract T load(NavigationTarget navigationTarget);

  /**
   * This should return model.getUri() in a typed way.
   * 
   * @return
   */
  protected abstract URI getUri();

  protected Disposable subscribeForChanges(URI uri, Consumer<URI> listener) {
    return null;
  }

  protected void notifyAllListeners() {
    data.notifyListeners();
  }

  @Override
  public ObservableObject data() {
    return data;
  }

  @Override
  public void initByNavigationTarget(NavigationTarget navigationTarget) {
    this.navigationTarget = navigationTarget;
    this.navigationTargetUUID = navigationTarget == null ? null : navigationTarget.getUuid();
    if (ref != null) {
      onCloseWindow();
      // throw new IllegalArgumentException("ref already initialized in ViewModel when
      // initByUUID!");
    }
    T loadedObject = load(navigationTarget);
    this.path = "";
    ref = new ApiObjectRef(null,
        loadedObject,
        apiBeanDescriptors);
    initCommon();
  }

  /**
   * Used when a child view model is created.
   */
  protected void initByParentRef(ViewModelImpl<?> parent, String path) {
    if (ref != null) {
      throw new IllegalArgumentException("ref already initialized in ViewModel when initByRef!");
    }
    this.path = path;
    this.parent = parent;
    ref = parent.ref.getValueRefByPath(path);
    ref.reevaluateChanges();
    initCommon();
  }

  /**
   * Common initialization, called after ref is set.
   */
  private void initCommon() {
    model = ref.getWrapper(modelClazz);
    data.setRef(ref);
    if (parent != null) {
      data.setParent(parent.data, path);
    }
    initChildViewModels();
    initCommands();
    initSubscription();
    notifyAllListeners();
  }

  protected void initChildViewModels() {
    // init child view models in implementations
  }

  protected void initSubscription() {
    URI uri = getUri();
    if (uri != null) {
      listener = this::updateModel;
      subscription = subscribeForChanges(uri, listener);
    }
  }

  protected void updateModel(URI updatedObjectUri) {
    if (ref == null) {
      throw new IllegalArgumentException("ref not initialized in ViewModel when updating!");
    }
    if (Objects.equals(getUri(), updatedObjectUri)) {
      T loadedObject = load(navigationTarget);
      ref.setObject(loadedObject);
      notifyAllListeners();
    }
  }

  @Override
  public void onCloseWindow() {
    if (subscription != null) {
      subscription.dispose();
      subscription = null;
    }
    if (ref != null) {
      ref = null;
      data.setRef(null);
      model = null;
      commandsByCode.clear();
    }
  }

  protected void registerCommand(String commandCode, Runnable command) {
    registerCommand(commandCode, modelClazz, o -> command.run());
  }

  protected void registerCommand(String commandCode, Consumer<T> command) {
    registerCommand(commandCode, modelClazz, command);
  }

  protected <O> void registerCommand(String commandCode, Class<O> clazz, Consumer<O> command) {
    registerCommand(null, commandCode, clazz, command);
  }

  protected <O> void registerCommand(String commandPath, String commandCode, Class<O> clazz,
      Consumer<O> command) {
    registerWrapperCommand(commandPath, commandCode, new WrapperCommand<>(command, clazz));
  }

  protected void registerCommandWithParams(String commandCode,
      Consumer<Object[]> command) {
    registerCommandWithParams(commandCode, modelClazz, (m, p) -> command.accept(p));
  }

  protected <O> void registerCommandWithParams(String commandCode, Class<O> clazz,
      BiConsumer<O, Object[]> command) {
    registerCommandWithParams(null, commandCode, clazz, command);
  }

  protected <O> void registerCommandWithParams(String commandPath, String commandCode,
      Class<O> clazz, BiConsumer<O, Object[]> command) {
    registerWrapperCommand(commandPath, commandCode, new WrapperCommand<>(command, clazz));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected void registerWrapperCommand(String commandPath, String commandCode,
      WrapperCommand wrapperCommand) {
    if (path == null) {
      throw new IllegalArgumentException("registerCommand called before initialization!");
    }
    if (parent != null) {
      parent.registerWrapperCommand(commandPathWithParent(commandPath), commandCode,
          wrapperCommand);
    } else {
      Map<String, WrapperCommand<?>> commandMap =
          commandsByCode.computeIfAbsent(commandCode, c -> new HashMap<>());
      commandMap.put(commandPath, wrapperCommand);
    }
  }

  private String commandPathWithParent(String commandPath) {
    if (commandPath == null) {
      return path;
    }
    return path + "/" + commandPath;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void executeCommand(String commandPath, String commandCode, Object... params) {
    WrapperCommand<?> command = findWrapperCommand(commandPath, commandCode);
    if (command != null) {
      command.execute(commandPath, commandCode, params);
    } else {
      super.executeCommand(commandPath, commandCode, params);
    }
    notifyAllListeners();
  }

  @SuppressWarnings("rawtypes")
  private WrapperCommand findWrapperCommand(String commandPath, String commandCode) {
    if (parent != null) {
      return parent.findWrapperCommand(commandPathWithParent(commandPath), commandCode);
    }
    WrapperCommand<?> command = null;
    Map<String, WrapperCommand<?>> commandMap = commandsByCode.get(commandCode);
    if (commandMap != null) {
      command = commandMap.get(commandPath);
      if (command == null) {
        // no exact match, find *
        String executedPath = commandPath;
        String matchingPath = commandMap.keySet().stream()
            .filter(p -> pathMatch(p, executedPath))
            .findFirst()
            .orElse(null);
        command = commandMap.get(matchingPath);
      }
    }
    return command;
  }

  private boolean pathMatch(String registeredPath, String executedPath) {
    if (executedPath == null || registeredPath == null) {
      return false;
    }
    if (registeredPath.endsWith("*")) {
      String prefix = registeredPath.substring(0, registeredPath.length() - 1);
      return executedPath.startsWith(prefix);
    }
    return false;
  }

  private class WrapperCommand<O> {

    private final BiConsumer<O, Object[]> commandWithParams;
    private final Consumer<O> commandWithoutParams;
    private final Class<O> clazz;

    public WrapperCommand(BiConsumer<O, Object[]> command, Class<O> clazz) {
      this.commandWithParams = command;
      this.commandWithoutParams = null;
      this.clazz = clazz;
    }

    public WrapperCommand(Consumer<O> command, Class<O> clazz) {
      this.commandWithParams = null;
      this.commandWithoutParams = command;
      this.clazz = clazz;
    }

    public void execute(String commandPath, String commandCode, Object... params) {
      Object value;
      if (parent != null) {
        if (commandPath == null) {
          commandPath = commandPathWithParent(commandPath);
        }
        if (!commandPath.startsWith(path)) {
          commandPath = commandPathWithParent(commandPath);
        }
        value = parent.ref.getValueRefByPath(commandPath).getWrapper(clazz);
      } else {
        value = ref.getValueRefByPath(commandPath).getWrapper(clazz);
      }
      O object = clazz.cast(value);
      if (commandWithParams != null) {
        commandWithParams.accept(object, params);
      } else {
        if (params != null && params.length > 0) {
          log.debug(
              "Command ({}) declared without params, but called with params!", commandCode);
        }
        commandWithoutParams.accept(object);
      }
    }
  }

  public void setObject(Object object) {
    ref.setObject(object);
    notifyAllListeners();
  }

  public Object getObject() {
    return ref.getObject();
  }

  public Class<T> getModelClazz() {
    return modelClazz;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V extends ViewModel> V getChild(String path) {
    return (V) childrenByPath.get(path);
  }

  public T getModel() {
    return model;
  }

  @Override
  public ViewModelData getViewModelData() {
    ViewModelData result = new ViewModelData()
        .uuid(navigationTargetUUID)
        .navigationTarget(navigationTarget)
        .path(path)
        .model(model);
    childrenByPath.forEach((p, vm) -> result.putChildrenItem(p, vm.getViewModelData()));
    return result;
  }

}
