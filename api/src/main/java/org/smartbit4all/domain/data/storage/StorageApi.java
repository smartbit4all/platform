package org.smartbit4all.domain.data.storage;

import java.net.URI;

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
   * @param scheme The scheme name.
   * @return The storage if it exists.
   */
  Storage get(String scheme);

  /**
   * Retrieves the {@link Storage} instance responsible for persisting in the given scheme.
   * Typically every module has a scheme used by the apis in the module.
   * 
   * @param uri The uri that defines the scheme to find the proper {@link Storage}.
   * @return The storage if it exists or else null.
   */
  Storage getStorage(URI uri);

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
  <T> StorageObject<T> load(URI uri, Class<T> clazz);

  /**
   * The StorageObject is loaded by the URI itself. In this case we are not sure about the type of
   * the object. It will be discovered by the persisted information.
   * 
   * @param uri The uri of the object to load.
   * @return
   */
  StorageObject<?> load(URI uri);

  /**
   * Creates an {@link ObjectHistoryIterator} that can iterate through the
   * {@link StorageObjectHistoryEntry}s of the object found with the given uri, making available to
   * investigate the full history of that object. In order from the most oldest to the most recent.
   * 
   * @param uri
   * @return
   */
  ObjectHistoryIterator objectHistory(URI uri);

  /**
   * Creates an {@link ObjectHistoryIterator} that can iterate through the
   * {@link StorageObjectHistoryEntry}s of the object found with the given uri, making available to
   * investigate the full history of that object. In order from the most recent to the oldest.
   * 
   * @param uri
   * @return
   */
  ObjectHistoryIterator objectHistoryReverse(URI uri);

  ObjectStorage getDefaultObjectStorage();

}
