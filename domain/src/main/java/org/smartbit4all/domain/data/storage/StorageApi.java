package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.Optional;

/**
 * The storage api is the access for the {@link Storage} instances defined in the configurations of
 * the system. The Storage are the logical unit of object storage all of them are identified by
 * their scheme. One logical Storage always have a physical {@link ObjectStorage} implementation in
 * the background to ensure atomic transaction on the save of the {@link StorageObject}.
 * 
 * @author Peter Boros
 */
public interface StorageApi {

  /**
   * Retrieves the {@link Storage} instance responsible for persisting in the given scheme.
   * Typically every module has a scheme used by the apis in the module.
   * 
   * @param clazz The class.
   * @return The storage if it exists.
   */
  Storage get(String scheme);

  // /**
  // * Generic function to load the referenced objects for a given URI.
  // *
  // * @param <T> The type of the referenced object we are looking for.
  // * @param uri The uri of the object that has the references.
  // * @param typeClass The type of the referenced objects. Must be a Java type available in the
  // given
  // * JVM!
  // * @return The set of referenced objects.
  // */
  // <T, R> Set<R> loadReferences(URI uri, Class<T> clazz, Class<R> typeClass);

  /**
   * This function can be used to load any object managed by the StorageApi. It identifies the given
   * {@link Storage} by the clazz and the URI. The uri can define the exact physical location of the
   * object and can define the {@link Storage} responsible for.
   * 
   * @param <T> The type
   * @param uri The uri of the object.
   * @param clazz The class of the object we need.
   * @return The object.
   */
  <T> Optional<StorageObject<T>> load(URI uri, Class<T> clazz);

  /**
   * The StorageObject is loaded by the URI itself. In this case we are not sure about the type of
   * the object. It will be discovered by the persisted information.
   * 
   * @param uri The uri of the object to load.
   * @return
   */
  Optional<StorageObject<?>> load(URI uri);

}
