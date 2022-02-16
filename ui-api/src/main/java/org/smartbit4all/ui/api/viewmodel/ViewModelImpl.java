package org.smartbit4all.ui.api.viewmodel;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class ViewModelImpl<T> extends ObjectEditingImpl implements ViewModel {

  private static final Logger log = LoggerFactory.getLogger(ViewModelImpl.class);

  // TODO handle path
  private String path;

  protected ObservableObjectImpl data;

  protected T model;

  private Class<T> modelClazz;

  protected Consumer<URI> listener;

  private Disposable subscription;

  private Map<Class<?>, ApiBeanDescriptor> apiBeanDescriptors;

  protected Map<String, Map<String, WrapperCommand<?>>> commandsByCode = new HashMap<>();

  protected ViewModelImpl(ObservablePublisherWrapper publisherWrapper,
      Map<Class<?>, ApiBeanDescriptor> apiBeanDescriptors,
      Class<T> modelClazz) {
    super();
    this.path = null;
    data = new ObservableObjectImpl(publisherWrapper);
    this.apiBeanDescriptors = apiBeanDescriptors;
    this.modelClazz = modelClazz;
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
  public void initByUUID(UUID uuid) {
    if (Objects.equals(uuid, navigationTargetUUID)) {
      if (ref != null) {
        throw new IllegalArgumentException("ref already initialized in ViewModel when initByUUID!");
      }
      T loadedObject = load(navigationTarget);
      this.path = "/";
      ref = new ApiObjectRef(null,
          loadedObject,
          apiBeanDescriptors);
      initCommon();
    } else {
      // remove navigationTargetUUID, navigationTarget?
    }
  }

  @Override
  public void initByParentRef(ApiObjectRef parentRef, String path) {
    if (ref != null) {
      throw new IllegalArgumentException("ref already initialized in ViewModel when initByRef!");
    }
    this.path = path;
    ref = parentRef.getValueRefByPath(path);
    initCommon();
  }

  /**
   * Common initialization, called after ref is set.
   */
  private void initCommon() {
    model = ref.getWrapper(modelClazz);
    data.setRef(ref);
    initCommands();
    initSubscription();
    notifyAllListeners();
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

  private <O> void registerWrapperCommand(String commandPath, String commandCode,
      WrapperCommand<O> wrapperCommand) {
    if (path == null) {
      throw new IllegalArgumentException("registerCommand called before initialization!");
    }
    if (commandPath == null) {
      commandPath = path;
    } else {
      commandPath = path + commandPath;
    }
    Map<String, WrapperCommand<?>> commandMap =
        commandsByCode.computeIfAbsent(commandPath, c -> new HashMap<>());
    commandMap.put(commandCode, wrapperCommand);
  }

  @Override
  public void executeCommand(String commandPath, String commandCode, Object... params) {
    if (commandPath == null) {
      commandPath = path;
    }
    Map<String, WrapperCommand<?>> commandMap = commandsByCode.get(commandPath);
    if (commandMap == null) {
      super.executeCommand(commandPath, commandCode, params);
    } else {
      WrapperCommand<?> command = commandMap.get(commandCode);
      if (command != null) {
        command.execute(commandPath, commandCode, params);
      } else {
        super.executeCommand(commandPath, commandCode, params);
      }
    }
    notifyAllListeners();
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

    public void execute(String path, String commandCode, Object... params) {
      Object value = ref.getValueRefByPath(path).getWrapper(clazz);
      O object = clazz.cast(value);
      if (commandWithParams != null) {
        commandWithParams.accept(object, params);
      } else {
        if (params != null && params.length > 0) {
          log.debug(
              "Command (" + commandCode + ") declared without params, but called with params!");
        }
        commandWithoutParams.accept(object);
      }
    }
  }
}
