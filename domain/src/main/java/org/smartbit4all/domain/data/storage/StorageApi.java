package org.smartbit4all.domain.data.storage;

/**
 * The storage api is the access for the {@link Storage} instances defined in the configurations of
 * the system.
 * 
 * TODO We must define more storage for the same class. They can be managed by
 * 
 * @author Peter Boros
 */
public interface StorageApi {

  /**
   * Retrieves the {@link Storage} instance responsible for persisting given class.
   * 
   * @param <T>
   * @param clazz The class.
   * @return The storage if it exists.
   */
  <T> Storage<T> get(Class<T> clazz);

}
