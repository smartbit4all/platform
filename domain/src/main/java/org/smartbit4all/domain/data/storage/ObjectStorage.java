package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.List;
import java.util.concurrent.locks.Lock;
import org.smartbit4all.core.object.ObjectDefinition;

/**
 * ObjectStorage can store serialized objects, identified by their URIs. The object storage instance
 * is important because the configured instance is responsible for accessing the persistent objects
 * in the physical storage. The configured instance is the basic item for this. The instance has an
 * uri to identify and this object is updated periodically while the given instance is active. The
 * instance is responsible for executing invocations. If an instance is getting down then the rest
 * of the instances will distribute its tasks and continue working. There are two main task that is
 * managed.
 * 
 * The first one is the transactions. If an instance has transactions then the post processing of
 * the transacted objects is managed by the given runtime. It can be useful to finish up the
 * transactions of a previously active instance. It depends on the transaction system of the
 * physical storage.
 * 
 * The other thing is the API calls attached to the successful transaction. These are channels
 * associated with instances. The channel can be associated to one instance or can be managed by all
 * instances. The execution model can be sequential or parallel.
 * 
 * The {@link ObjectStorage} can manage set of objects also. It is defined in the URI as a sub path
 * inside the alias of the class. Looks like this:
 * 
 * <p>
 * storagescheme:/org_smartbit4all_module_MyClass<b>/active</b>/2022...
 * </p>
 * 
 * The object storage can read all the objects in from a set with the
 * {@link #readAll(Storage, String, Class)} operation.
 * 
 * @author Zoltan Szegedi
 *
 */
public interface ObjectStorage {

  /**
   * Save the object. The {@link StorageObject} always has an URI but the content is not necessarily
   * filled. TODO: clarify! "If we add a StorageObject#isDelete() == true object or with a null
   * object then it results a version where the content is empty. We can call this deleted but it's
   * not really the deletion."
   */
  URI save(StorageObject<?> object);

  /**
   * Load the object with the given URI.
   * 
   * @param storage The logical storage of the given operation.
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   * @param options When loading we can instruct the loading to retrieve the necessary data only.
   * 
   */
  <T> StorageObject<T> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options);

  /**
   * Load the object with the given URI.
   * 
   * @param storage The logical storage of the given operation.
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param options When loading we can instruct the loading to retrieve the necessary data only.
   * @return We try to identify the class from the URI itself.
   */
  StorageObject<?> load(Storage storage, URI uri, StorageLoadOption... options);

  /**
   * Load the objects with the given URI.
   * 
   * @param storage The logical storage of the given operation.
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
   * @param storage The logical storage of the given operation.
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   */
  <T> T read(Storage storage, URI uri, Class<T> clazz);

  /**
   * Read the given object identified by the URI. We can not initiate a transaction with the result
   * of the read! Use the load instead.
   * 
   * @param storage The logical storage of the given operation.
   * @param uri The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @return We try to identify the class from the URI itself.
   */
  Object read(Storage storage, URI uri);

  /**
   * Read the given objects identified by the URI. We can not initiate a transaction with the result
   * of the read! Use the load instead.
   * 
   * @param storage The logical storage of the given operation.
   * @param uris The Unified Resource Identifier of the object we are looking for. It must be
   *        situated in the current physical storage. This routing relies on the registry of the
   *        {@link StorageApi} and based on the scheme of the URI.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   */
  <T> List<T> read(Storage storage, List<URI> uris, Class<T> clazz);

  /**
   * Read the given objects identified by the URI. We can not initiate a transaction with the result
   * of the read! Use the load instead.
   * 
   * @param storage The logical storage of the given operation.
   * @param setName The name of the set that the given call is looking for. If there is no item in
   *        the set or the set itself doesn't exist an empty list is going to be returned.
   * @param clazz The class of the object to load. Based on this class we can easily identify the
   *        {@link ObjectDefinition} responsible for this type of objects.
   */
  <T> List<T> readAll(Storage storage, String setName, Class<T> clazz);

  /**
   * Mive the given object inside the object storage.
   * 
   * @param srcStorage The source storage.
   * @param uri The uri of the object.
   * @param targetStorage The archive storage as the target of the move operation.
   * @param targetUri The archive uri of the object.
   * @return true if the move was successful.
   */
  boolean move(Storage srcStorage, URI uri, Storage targetStorage, URI targetUri);

  /**
   * @return Return true if the given {@link ObjectStorage} is the default one by the configuration.
   *         If there is more then one default then the application won't start!
   */
  boolean isDefaultStorage();

  /**
   * Perform a quick check for existence of a given uri.
   * 
   * @param uri The object uri to check.
   * @return Return true if the given entry exists. Doesn't check the consistency of the data
   *         because it's not loading data itself.
   */
  boolean exists(URI uri);

  /**
   * Retrieves the last modification date (time) for the given object.
   * 
   * @param uri The object uri.
   * @return Return the modification time based on the storage time. Null if the given URI doesn't
   *         exist
   */
  Long lastModified(URI uri);

  /**
   * Retrieve the {@link StorageObjectLock} attached to the given object URI. This is just the Lock
   * object that should be used as a normal {@link Lock} implementation.
   * 
   * @param objectUri The object URI the lock is attached to.
   * @return The {@link StorageObjectLock} object for tha
   */
  StorageObjectLock getLock(URI objectUri);

  /**
   * Creates an {@link ObjectHistoryIterator} that can iterate through the
   * {@link StorageObjectHistoryEntry}s of the object found with the given uri, making available to
   * investigate the full history of that object. In order from the oldest version to the most
   * recent.
   * 
   * @param uri
   * @param definition
   * @return
   */
  ObjectHistoryIterator objectHistory(URI uri, ObjectDefinition<?> definition);

  /**
   * Creates an {@link ObjectHistoryIterator} that can iterate through the
   * {@link StorageObjectHistoryEntry}s of the object found with the given uri, making available to
   * investigate the full history of that object. In order from the most recent version to the
   * oldest.
   * 
   * @param uri
   * @param definition
   * @return
   */
  ObjectHistoryIterator objectHistoryReverse(URI uri, ObjectDefinition<?> definition);

}
