package org.smartbit4all.domain.data.storage;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import org.smartbit4all.api.collection.StoredSequence;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectDefinitionApi;

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
   * The object in the storage by their URI as key.
   */
  private Map<URI, Map<String, StorageObjectReferenceEntry>> referencesByURI =
      new ConcurrentHashMap<>();

  private Map<URI, Map<String, Map<String, StorageObjectReferenceEntry>>> collectionsByURI =
      new ConcurrentHashMap<>();

  public ObjectStorageInMemory(ObjectDefinitionApi objectDefinitionApi) {
    super(objectDefinitionApi);
  }

  @Override
  public StorageObject<?> save(StorageObject<?> storageObject) {
    if (storageObject == null) {
      return null;
    }
    // Only put the original Object into to Map. Unwrap if wrapped.
    StorageObjectLock objectLock = getLock(storageObject.getUri());
    objectLock.lock();
    try {
      StorageObject<?> copy = storageObject.copy();
      // TODO handle versions...
      // copy.getVersion().setSerialNoData(storageObject.getVersion().getSerialNoData() + 1);
      copy.setObjectObj(ApiObjectRef.unwrapObject(copy.getObject()));
      Object oldObj = getOldObj(objectsByURI.get(copy.getUri()));
      URI uri = copy.getUri();
      objectsByURI.put(uri, copy);
      referencesByURI.put(uri, storageObject.getReferences());
      collectionsByURI.put(uri, storageObject.getCollections());
      invokeOnSucceedFunctions(storageObject,
          new StorageSaveEvent(
              () -> storageObject.getVersionUri(),
              () -> oldObj,
              copy.getVersionUri(),
              ApiObjectRef.unwrapObject(copy.getObject()),
              storageObject.definition().getClazz()));
      return copy;
    } finally {
      objectLock.unlock();
    }
  }

  private Object getOldObj(StorageObject<?> oldObj) {
    Object unwrappedOldObj = null;
    if (oldObj != null) {
      Object obj = oldObj.getObject();
      if (obj != null) {
        unwrappedOldObj = ApiObjectRef.unwrapObject(obj);
      }
    }
    return unwrappedOldObj;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> StorageObject<T> load(Storage storage, URI uri, Class<T> clazz,
      StorageLoadOption... options) {
    StorageObject<T> storageObject = (StorageObject<T>) objectsByURI.get(uri);
    if (storageObject == null) {
      throw new ObjectNotFoundException(uri, clazz, null);
    }
    StorageObject<?> copy = storageObject.copy();
    copy.setObjectObj(ApiObjectRef.unwrapObject(copy.getObject()));
    copy.setReferences(referencesByURI.get(uri)); // FIXME should copy these too?
    copy.setCollections(collectionsByURI.get(uri));
    return (StorageObject<T>) copy;
  }

  @Override
  public boolean exists(URI uri) {
    return objectsByURI.containsKey(uri);
  }

  @Override
  public <T> List<URI> readAllUris(Storage storage, String setName, Class<T> clazz) {
    return readAll(storage, setName, clazz, u -> u);
  }

  @Override
  public <T> List<T> readAll(Storage storage, String setName, Class<T> clazz) {
    return readAll(storage, setName, clazz, u -> read(storage, u, clazz));
  }

  private <O> List<O> readAll(Storage storage, String setName, Class<?> clazz,
      Function<URI, O> reader) {
    // TODO Auto-generated method stub
    return Collections.emptyList();
  }

  @Override
  protected void saveSingleVersionObject(StorageObject<?> object) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  protected URI saveVersionedObject(StorageObject<?> object) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StoredSequence getSequence(String schema, String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StorageObjectPhysicalLock lockPhysicalObject(URI objectUri, long waitUntil) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void unlockPhysicalObject(StorageObjectPhysicalLock lock) {
    // TODO Auto-generated method stub
  }

  @Override
  protected Function<Boolean, StorageObjectPhysicalLock> physicalLockSupplier(URI objectUri) {
    return null;
  }

  @Override
  protected Consumer<StorageObjectPhysicalLock> physicalLockReleaser() {
    return null;
  }
}
