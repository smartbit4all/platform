package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.api.storage.bean.StorageObjectRelationData;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.domain.data.storage.StorageObject.StorageObjectOperation;

/**
 * The abstract basic implementation of the {@link ObjectStorage}.
 * 
 * @author Peter Boros
 */
public abstract class ObjectStorageImpl implements ObjectStorage {

  private static final Logger log = LoggerFactory.getLogger(ObjectStorageImpl.class);

  /**
   * These locks are the in memory locks holding the file system level lock. We need this to avoid
   * OverlappingFileLockException caused by locking the same file in the same JVM. The file locks
   * belong to an operating system process.
   */
  private Map<URI, StorageObjectLock> locks = new HashMap<>();

  /**
   * The operations on the locks are exclusive.
   */
  private Lock lockMutex = new ReentrantLock(true);

  protected Supplier<StorageObjectPhysicalLock> getAcquire() {
    return null;
  }

  protected Consumer<StorageObjectPhysicalLock> getReleaser() {
    return null;
  }

  /**
   * The object api gives access to the meta data of the objects.
   */
  protected final ObjectApi objectApi;

  protected boolean defaultStorage = false;

  public ObjectStorageImpl(ObjectApi objectApi) {
    super();
    this.objectApi = objectApi;
  }

  protected StorageObjectLock acquire(URI objectUri) {
    lockMutex.lock();
    StorageObjectLock storageObjectLock;
    try {
      storageObjectLock = locks.computeIfAbsent(objectUri,
          u -> new StorageObjectLock(u, uri -> {
            lockMutex.lock();
            try {
              locks.remove(uri);
            } finally {
              lockMutex.unlock();
            }
          }, getAcquire(), getReleaser()));
    } finally {
      lockMutex.unlock();
    }
    // Depending on the state we try to acquire the exclusive right.
    if (storageObjectLock.enter()) {
      return storageObjectLock;
    } else {
      // Try to acquire again because we found a destroyed instance of the lock.
      return acquire(objectUri);
    }
  }

  @Override
  public <T> List<StorageObject<T>> load(Storage storage, List<URI> uris, Class<T> clazz,
      StorageLoadOption... options) {
    return uris.parallelStream().map(u -> load(storage, u, clazz, options))
        .filter(o -> o.isPresent())
        .map(o -> o.get())
        .collect(Collectors.toList());
  }

  @Override
  public Optional<StorageObject<?>> load(Storage storage, URI uri, StorageLoadOption... options) {
    if (uri == null || uri.getScheme() == null || uri.getScheme().isEmpty()) {
      return Optional.empty();
    }
    String scheme = uri.getScheme();
    if (scheme == null) {
      log.debug("Unable to load {} uri, the Storage not found by the scheme", uri);
      return Optional.empty();
    }

    // Try to identify the ObjectDefintion by the URI
    ObjectDefinition<?> objectDefinition = objectApi.definition(uri);
    Optional<StorageObject<?>> result = Optional.empty();
    if (objectDefinition != null) {
      Optional<?> load = load(storage, uri, objectDefinition.getClazz(), options);
      if (load.isPresent()) {
        result = Optional.of((StorageObject<?>) load.get());
      }
    }
    return result;
  }

  @Override
  public <T> Optional<T> read(Storage storage, URI uri, Class<T> clazz) {
    Optional<StorageObject<T>> load = load(storage, uri, clazz);
    return load.isPresent() ? Optional.ofNullable(load.get().getObject()) : Optional.empty();
  }

  @Override
  public Optional<?> read(Storage storage, URI uri) {
    Optional<StorageObject<?>> load = load(storage, uri);
    return load.isPresent() ? Optional.ofNullable(load.get().getObject()) : Optional.empty();
  }

  @Override
  public <T> List<T> read(Storage storage, List<URI> uris, Class<T> clazz) {
    List<StorageObject<T>> load = load(storage, uris, clazz);
    return load.stream().map(s -> s.getObject()).filter(o -> o != null)
        .collect(Collectors.toList());
  }

  protected <T> StorageObject<T> instanceOf(Storage storage, ObjectDefinition<T> objectDefinition,
      T object) {
    StorageObject<T> storageObject = new StorageObject<>(objectDefinition, storage);
    storageObject.setObjectInner(object);
    return storageObject;
  }

  protected <T> StorageObject<T> instanceOf(Storage storage, ObjectDefinition<T> objectDefinition,
      URI objectUri) {
    StorageObject<T> storageObject = new StorageObject<>(objectDefinition, storage);
    storageObject.setUri(objectUri);
    return storageObject;
  }

  @Override
  public boolean isDefaultStorage() {
    return defaultStorage;
  }

  protected final void setDefaultStorage(boolean defaultStorage) {
    this.defaultStorage = defaultStorage;
  }

  /**
   * The {@link StorageObjectData} is prepared by the {@link StorageObject} to have a complete
   * reference and collection list.
   * 
   * @param <T>
   * @param storageObject The storage object from the request.
   * @param storageObjectData The {@link StorageObjectData}
   */
  protected final <T> StorageObjectRelationData saveStorageObjectReferences(
      StorageObject<T> storageObject, StorageObjectRelationData relationData) {
    Map<String, ObjectReference> currentReferences =
        relationData != null ? relationData.getReferences() : new HashMap<>();
    for (Entry<String, StorageObjectReferenceEntry> entry : storageObject.getReferences()
        .entrySet()) {
      if (!entry.getValue().isDelete()) {
        // If we have an object and the ObjctReference is not set we save the object and construct
        // the ObjectReference.
        if (entry.getValue().getObject() != null && entry.getValue().getReferenceData() == null) {
          // Save the referred object.
          Object refObject = entry.getValue().getObject();
          Storage storage = storageObject.getStorage();
          StorageObject<? extends Object> soRefObject = storage.instanceOf(refObject.getClass());
          soRefObject.setObjectObj(refObject);
          URI refObjectUri = storage.save(soRefObject);
          // Set the entry.
          entry.getValue().setReferenceData(new ObjectReference().uri(refObjectUri));
        }
        //
        if (entry.getValue().getReferenceData() != null) {
          // This is new reference and add it to the map if not empty.
          currentReferences.put(entry.getKey(), entry.getValue().getReferenceData());
        }
      } else {
        ObjectReference objectReference = currentReferences.get(entry.getKey());
        if (objectReference != null) {
          currentReferences.remove(entry.getKey());
        }
      }
    }
    // The collections are almost the same.
    Map<String, ObjectReferenceList> currentCollections =
        relationData != null ? relationData.getCollections() : new HashMap<>();
    for (Entry<String, Map<String, StorageObjectReferenceEntry>> entry : storageObject
        .getCollections().entrySet()) {
      List<ObjectReference> finalList =
          entry.getValue().values().stream().filter(e -> !e.isDelete()).filter(e -> {
            return e.getReferenceData() != null;
          }).map(e -> e.getReferenceData()).collect(Collectors.toList());
      if (finalList.isEmpty()) {
        currentCollections.remove(entry.getKey());
      } else {
        ObjectReferenceList referenceList = currentCollections
            .computeIfAbsent(entry.getKey(), k -> new ObjectReferenceList());
        referenceList.setReferences(finalList);
      }
    }

    StorageObjectRelationData result = null;
    if (!currentReferences.isEmpty() || !currentCollections.isEmpty()) {
      result = relationData != null ? relationData : new StorageObjectRelationData();
      result.setCollections(currentCollections);
      result.setReferences(currentReferences);
      result.setUri(URI.create(storageObject.getUri().toString() + "#relations"));
    }
    return result;

  }

  protected final <T> void loadStorageObjectReferences(StorageObject<T> storageObject,
      StorageObjectRelationData storageObjectRelationData) {
    Map<String, ObjectReference> currentReferences = storageObjectRelationData.getReferences();
    if (currentReferences != null) {
      for (Entry<String, ObjectReference> entry : currentReferences.entrySet()) {
        storageObject.setReference(entry.getKey(), entry.getValue());
      }
    }
    Map<String, ObjectReferenceList> collections = storageObjectRelationData.getCollections();
    if (collections != null) {
      for (Entry<String, ObjectReferenceList> entry : collections.entrySet()) {
        Map<String, StorageObjectReferenceEntry> collectionSet = new HashMap<>();
        storageObject.getCollections().put(entry.getKey(), collectionSet);
        if (entry.getValue().getReferences() != null) {
          for (ObjectReference reference : entry.getValue().getReferences()) {
            collectionSet.put(reference.getReferenceId(),
                new StorageObjectReferenceEntry(reference));
          }
        }
      }
    }
  }

  protected final void setOperation(StorageObject<?> object, StorageObjectOperation operation) {
    if (object != null) {
      object.setOperation(operation);
    }
  }

}
