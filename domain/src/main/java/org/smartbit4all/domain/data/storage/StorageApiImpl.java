package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The storage api is the access for the {@link Storage} instances defined in the configurations of
 * the system.
 * 
 * @author Peter Boros
 */
public final class StorageApiImpl implements StorageApi, InitializingBean {

  /**
   * All the storages we have in the configuration. Autowired by spring.
   */
  @Autowired(required = false)
  private List<Storage> storages;

  /**
   * The missing {@link Storage}s will be constructed if they are missing from the configuration. We
   * need the {@link #defaultObjectStorage} variable for this.
   */
  private ReadWriteLock rwlStorages = new ReentrantReadWriteLock();

  @Autowired(required = false)
  private List<ObjectStorage> objectStorages;

  /**
   * The preprocessed storages identified by the class.
   */
  private Map<String, Storage> storagesByScheme = new HashMap<>();

  /**
   * The default object storage. If there is only one {@link ObjectStorage} defined in the
   * configuration then it will be used. Else there must be exactly one {@link ObjectStorage}
   * defined as default by the {@link ObjectStorageImpl#setDefaultStorage(boolean)}.
   */
  private ObjectStorage defaultObjectStorage;

  @Autowired
  private ObjectApi objectApi;

  @Override
  public void afterPropertiesSet() throws Exception {
    if (storages != null) {
      for (Storage storage : storages) {
        storagesByScheme.put(storage.getScheme(), storage);
      }
    }
    if (objectStorages != null) {
      if (objectStorages.size() == 1) {
        defaultObjectStorage = objectStorages.get(0);
      } else {
        for (ObjectStorage objectStorage : objectStorages) {
          if (objectStorage.isDefaultStorage()) {
            if (defaultObjectStorage == null) {
              defaultObjectStorage = objectStorage;
            } else {
              throw new IllegalStateException(
                  "There is more then one default object storage defined (" + defaultObjectStorage
                      + " and " + objectStorage + ")");
            }
          }
        }
      }
    }
  }

  @Override
  public Storage get(String scheme) {
    Storage storage;
    rwlStorages.readLock().lock();
    try {
      storage = storagesByScheme.get(scheme);
    } finally {
      rwlStorages.readLock().unlock();
    }
    if (storage == null) {
      if (defaultObjectStorage == null) {
        throw new IllegalStateException("The storage for the " + scheme
            + " scheme is not configured and can not be created because the default storage is missing.");
      }
      rwlStorages.writeLock().lock();
      try {
        storage = storagesByScheme.get(scheme);
        if (storage == null) {
          storage = new Storage(scheme, objectApi, defaultObjectStorage);
          storagesByScheme.put(scheme, storage);
        }
      } finally {
        rwlStorages.writeLock().unlock();
      }
    }
    return storage;
  }

  // @Override
  // public <T, R> Set<R> loadReferences(URI uri, Class<T> clazz, Class<R> typeClass) {
  // try {
  //
  // Optional<ObjectReferenceList> optReferences =
  // get(clazz).loadReferences(uri, typeClass.getName());
  //
  // List<URI> uriList = null;
  // if (optReferences.isPresent()) {
  // uriList = optReferences.get().getReferences().stream()
  // .map(r -> URI.create(r.getReferenceId())).collect(Collectors.toList());
  // } else {
  // uriList = Collections.emptyList();
  // }
  //
  // return new HashSet<>(
  // get(typeClass)
  // .load(uriList));
  // } catch (Exception e) {
  // throw new RuntimeException(
  // "Unable to load referenced objects for " + uri + " typeClass = " + typeClass.getName(),
  // e);
  // }
  // }

  @Override
  public StorageObject<?> load(URI uri) {
    Storage storage = getStorageByUri(uri);

    return storage.load(uri);
  }

  private final Storage getStorageByUri(URI uri) {
    if (uri == null || uri.getScheme() == null || uri.getScheme().isEmpty()) {
      throw new ObjectNotFoundException(uri, null, "Bad uri format");
    }
    String scheme = uri.getScheme();
    Storage storage = get(scheme);
    if (storage == null) {
      throw new ObjectNotFoundException(uri, null,
          "Unable to find storage scheme by uri. Might be a missing configuration.");
    }
    return storage;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> StorageObject<T> load(URI uri, Class<T> clazz) {
    StorageObject<?> loadResult = load(uri);
    if (!clazz.equals(loadResult.definition().getClazz())) {
      throw new IllegalArgumentException(
          "The class (" + loadResult.definition() + ") of loaded object identified by the "
              + uri + " is not what is expected. (" + clazz + ")");
    }
    return (StorageObject<T>) loadResult;
  }

  /**
   * Creates an {@link ObjectHistoryIterator} that can iterate through the
   * {@link StorageObjectHistoryEntry}s of the object found with the given uri, making available to
   * investigate the full history of that object. In order from the oldest version to the most
   * recent.
   * 
   * @param uri
   * @return
   */
  @Override
  public ObjectHistoryIterator objectHistory(URI uri) {
    Storage storage = getStorageByUri(uri);
    return storage.objectHistory(uri);
  }


  /**
   * Creates an {@link ObjectHistoryIterator} that can iterate through the
   * {@link StorageObjectHistoryEntry}s of the object found with the given uri, making available to
   * investigate the full history of that object. In order from the most recent version to the
   * oldest.
   * 
   * @param uri
   * @return
   */
  @Override
  public ObjectHistoryIterator objectHistoryReverse(URI uri) {
    Storage storage = getStorageByUri(uri);
    return storage.objectHistoryReverse(uri);
  }

  @Override
  public final ObjectStorage getDefaultObjectStorage() {
    return defaultObjectStorage;
  }
}
