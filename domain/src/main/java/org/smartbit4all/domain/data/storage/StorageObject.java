package org.smartbit4all.domain.data.storage;

import java.net.URI;
import org.smartbit4all.core.object.ObjectDefinition;

/**
 * 
 * The wrapper object for storage operations.
 * 
 * @author Peter Boros
 * @param <T>
 */
public final class StorageObject<T> {

  /**
   * The URI of the object.
   */
  private URI uri;

  private final ObjectDefinition<T> definition;

  public StorageObject(ObjectDefinition<T> objectDefinition) {
    this.definition = objectDefinition;
  }

  protected final URI getUri() {
    return uri;
  }

  protected final void setUri(URI uri) {
    this.uri = uri;
  }

  protected final ObjectDefinition<T> definition() {
    return definition;
  }

}
