package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.smartbit4all.core.object.ObjectDefinition;

/**
 * ObjectStorage can store serialized objects, identified by their URIs.
 * 
 * @author Zoltan Szegedi
 *
 * @param <T> Type of the object, which can be stored with the ObjectStorage.
 */
public interface ObjectStorage {

  /**
   * Save the object. The {@link StorageObject} always has an URI but the content is not necessarily
   * filled. If we add a {@link StorageObject#isDelete()} == true object or with a null object then
   * it results a version where the content is empty. We can call this deleted but it's not really
   * the deletion.
   */
  URI save(StorageObject<?> object);

  /**
   * Load the object with the given URI.
   * 
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   * @param options When loading we can instruct the loading to retrieve the necessary data only.
   * 
   */
  <T> Optional<StorageObject<T>> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options);

  /**
   * Load the object with the given URI.
   * 
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param options When loading we can instruct the loading to retrieve the necessary data only.
   * @return We try to identify the class from the URI itself.
   */
  Optional<StorageObject<?>> load(Storage storage, URI uri, StorageLoadOption... options);

  /**
   * Load the objects with the given URI.
   * 
   * @param uris The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   * @param options When loading we can instruct the loading to retrieve the necessary data only.
   */
  <T> List<StorageObject<T>> load(Storage storage, List<URI> uris, Class<T> clazz,
      StorageLoadOption... options);

  /**
   * Read the given object identified by the URI. We can not initiate a transaction with the result
   * of the read! Use the load instead.
   * 
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   */
  <T> Optional<T> read(Storage storage, URI uri, Class<T> clazz);

  /**
   * Read the given object identified by the URI. We can not initiate a transaction with the result
   * of the read! Use the load instead.
   * 
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @return We try to identify the class from the URI itself.
   */
  Optional<?> read(Storage storage, URI uri);

  /**
   * Read the given objects identified by the URI. We can not initiate a transaction with the result
   * of the read! Use the load instead.
   * 
   * @param uris The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   */
  <T> List<T> read(Storage storage, List<URI> uris, Class<T> clazz);

  /**
   * @return Return true if the given {@link ObjectStorage} is the default one by the configuration.
   *         If there is more then one default then the application won't start!
   */
  boolean isDefaultStorage();

}
