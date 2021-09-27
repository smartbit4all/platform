package org.smartbit4all.domain.data.storage;

import java.net.URI;
import org.smartbit4all.core.object.BeanMeta;

/**
 * 
 * The wrapper object for storage operations.
 * 
 * @author Peter Boros
 * @param <T>
 */
public class StorageObject<T> {

  /**
   * The URI of the object.
   */
  private URI uri;

  /**
   * This is the class of the object.
   */
  private Class<?> clazz;

  private BeanMeta meta;

  public <T> StorageObject(T object) {
    clazz = object.getClass();
    BeanMeta meta = StorageApiUtils.meta(clazz);
  }

  protected final URI getUri() {
    return uri;
  }

  protected final void setUri(URI uri) {
    this.uri = uri;
  }

  protected final Class<T> getClazz() {
    return (Class<T>) clazz;
  }

}
