package org.smartbit4all.core.object;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    this.path = "/";
    data = new ObservableObjectImpl(publisherWrapper);
    this.apiBeanDescriptors = apiBeanDescriptors;
    this.modelClazz = modelClazz;
  }

  protected abstract T load(URI uri);

  protected abstract Disposable subscribeForChanges(URI uri, Consumer<URI> listener);

  protected void notifyAllListeners() {
    data.notifyListeners();
  }

  @Override
  public ObservableObject data() {
    return data;
  }

  @Override
  public void init(URI objectUri) {
    T loadedObject = load(objectUri);
    if (ref == null) {

      ref = new ApiObjectRef(null,
          loadedObject,
          apiBeanDescriptors);
      model = ref.getWrapper(modelClazz);
      data.setRef(ref);

      listener = this::init;
      subscription = subscribeForChanges(objectUri, listener);
    } else {
      ref.setObject(loadedObject);
    }
    notifyAllListeners();
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
