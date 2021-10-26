package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectApi;

/**
 * Simple Map based implementation of object storage. It can be used for testing with storage. There
 * is no real transactions because it's not persisted.
 * 
 * @author Zoltan Szegedi
 *
 */
public class ObjectStorageInMemory extends ObjectStorageImpl {

  /**
   * The object in the storage by their URI as key.
   */
  private Map<URI, StorageObject<?>> objectsByURI = new ConcurrentHashMap<>();

  public ObjectStorageInMemory(ObjectApi objectApi) {
    super(objectApi);
  }

  @Override
  public URI save(StorageObject<?> storageObject) {
    // Only put the original Object into to Map. Unwrap if wrapped.
    StorageObjectLock objectLock = acquire(storageObject.getUri());
    try {
      StorageObject<?> copy = storageObject.copy();
      copy.setObjectObj(ApiObjectRef.unwrapObject(copy.getObject()));
      StorageObject<?> oldVersion = objectsByURI.get(copy.getUri());
      objectsByURI.put(copy.getUri(), copy);
      invokeOnSucceedFunctions(storageObject, new StorageSaveEvent<>(
          () -> oldVersion != null ? oldVersion.getObject() : null, copy.getObject()));
      return copy.getUri();
    } finally {
      objectLock.leave();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> StorageObject<T> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options) {
    return (StorageObject<T>) objectsByURI.get(uri);
  }

  @Override
  public List<ObjectHistoryEntry> loadHistory(Storage storage, URI uri) {
    return Collections.emptyList();
  }

  @Override
  public boolean exists(URI uri) {
    return objectsByURI.containsKey(uri);
  }

}
