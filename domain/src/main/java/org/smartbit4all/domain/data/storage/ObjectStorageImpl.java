package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The abstract basic implementation of the {@link ObjectStorage}.
 * 
 * @author Peter Boros
 */
public abstract class ObjectStorageImpl<T> implements ObjectStorage<T> {

  /**
   * The accessor method for the URI.
   */
  protected final Function<T, URI> uriAccessor;

  /**
   * The accessor method for the URI.
   */
  protected BiConsumer<T, URI> uriMutator;

  protected ObjectUriProvider<T> uriProvider;

  public ObjectStorageImpl(Function<T, URI> uriAccessor, ObjectUriProvider<T> uriProvider) {
    super();
    this.uriAccessor = uriAccessor;
    this.uriProvider = uriProvider;
  }

  public ObjectStorageImpl(Function<T, URI> uriAccessor) {
    super();
    this.uriAccessor = uriAccessor;
  }

  @Override
  public ObjectStorage<T> setUriMutator(BiConsumer<T, URI> mutator) {
    uriMutator = mutator;
    return this;
  }

  protected URI constructUri(T object, URI currentUri) {
    if (currentUri == null && uriProvider != null) {
      URI uri = uriProvider.constructUri(object);
      if (uriMutator != null) {
        uriMutator.accept(object, uri);
      }
      return uri;
    }
    return currentUri;
  }

  @Override
  public final ObjectUriProvider<T> getUriProvider() {
    return uriProvider;
  }

  public final void setUriProvider(ObjectUriProvider<T> uriProvider) {
    this.uriProvider = uriProvider;
  }

  @Override
  public URI getObjectUri(T Object) {
    return uriAccessor == null ? null : uriAccessor.apply(Object);
  }

}
