package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.core.object.ObjectDefinition;

/**
 * ObjectStorage can store serialized objects, identified by their URIs.
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
  <T> T read(Storage storage, URI uri, Class<T> clazz);

  /**
   * Read the given object identified by the URI. We can not initiate a transaction with the result
   * of the read! Use the load instead.
   * 
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

  /**
   * This function collects all the versions of an object and constructs the
   * {@link ObjectHistoryEntry} from the version. It will contains a summary of the data and the
   * changes come with the new version.
   * 
   * @param storage The storage that is requesting the history.
   * @param uri The uri of the object.
   * @return The list of history entries. It mustn't be null, all the implementations must return
   *         {@link Collections#emptyList()} instead of null.
   */
  List<ObjectHistoryEntry> loadHistory(Storage storage, URI uri, ObjectDefinition<?> defnition);

  /**
   * Perform a quick check for existence of a given uri.
   * 
   * @param uri The object uri to check.
   * @return Return true if the given entry exists. Doesn't check the consistency of the data
   *         because it's not loading data itself.
   */
  boolean exists(URI uri);

  /**
   * Retrieve the {@link StorageObjectLock} attached to the given object URI. This is just the Lock
   * object that should be used as a normal {@link Lock} implementation.
   * 
   * @param objectUri The object URI the lock is attached to.
   * @return The {@link StorageObjectLock} object for tha
   */
  StorageObjectLock getLock(URI objectUri);

}
