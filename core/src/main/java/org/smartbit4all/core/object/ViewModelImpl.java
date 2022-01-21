package org.smartbit4all.core.object;

import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class ViewModelImpl<T> extends ObjectEditingImpl implements ViewModel {

  // TODO handle path
  // private String path;

  protected ObservableObjectImpl data;

  protected T model;

  private Class<T> modelClazz;

  private Consumer<URI> listener;

  private Disposable subscription;

  private Map<Class<?>, ApiBeanDescriptor> apiBeanDescriptors;

  public ViewModelImpl(ObservablePublisherWrapper publisherWrapper,
      Map<Class<?>, ApiBeanDescriptor> apiBeanDescriptors,
      Class<T> modelClazz) {
    super();
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

      listener = uri -> init(uri);
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

}
