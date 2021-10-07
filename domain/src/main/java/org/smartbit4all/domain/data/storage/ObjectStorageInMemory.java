package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;
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

  /**
   * The references in the storage by their URI as key.
   */
  private Map<URI, Map<String, ObjectReferenceList>> referencesByURI = new ConcurrentHashMap<>();;

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
      objectsByURI.put(copy.getUri(), copy);
      return copy.getUri();
    } finally {
      objectLock.leave();
    }
  }

  @Override
  public <T> Optional<StorageObject<T>> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options) {
    @SuppressWarnings("unchecked")
    StorageObject<T> storageObject = (StorageObject<T>) objectsByURI.get(uri);
    return Optional.ofNullable(storageObject);
  }

}
