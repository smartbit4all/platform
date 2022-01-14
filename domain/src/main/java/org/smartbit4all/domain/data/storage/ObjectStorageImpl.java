package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.api.storage.bean.StorageObjectRelationData;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.StorageObject.StorageObjectOperation;
import org.springframework.beans.factory.annotation.Autowired;

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
  private Map<URI, StorageObjectLockEntry> locks = new HashMap<>();

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

  protected Supplier<String> versionCreatedBy = () -> StringConstant.UNKNOWN;

  protected boolean defaultStorage = false;

  @Autowired(required = false)
  private List<ObjectStorageSaveSucceedListener> onSucceedListeners;

  public ObjectStorageImpl(ObjectApi objectApi) {
    super();
    this.objectApi = objectApi;
  }

  @Override
  public StorageObjectLock getLock(URI objectUri) {
    lockMutex.lock();
    try {
      StorageObjectLockEntry entry = locks.get(objectUri);
      if (entry == null) {
        final StorageObjectLockEntry newEntry =
            new StorageObjectLockEntry(objectUri, getAcquire(), getReleaser());
        // TODO implement rather a complete object to hold object locks!
        newEntry.setLockRemover(uri -> {
          lockMutex.lock();
          try {
            if (newEntry.isEmpty()) {
              locks.remove(uri);
            }
          } finally {
            lockMutex.unlock();
          }
        });
        locks.put(objectUri, newEntry);
        entry = newEntry;
      }
      return entry.getLock();
    } finally {
      lockMutex.unlock();
    }
  }

  @Override
  public <T> List<StorageObject<T>> load(Storage storage, List<URI> uris, Class<T> clazz,
      StorageLoadOption... options) {
    return uris.parallelStream().map(u -> load(storage, u, clazz, options))
        .collect(Collectors.toList());
  }

  @Override
  public StorageObject<?> load(Storage storage, URI uri, StorageLoadOption... options) {
    ObjectDefinition<?> objectDefinition = getDefinitionFromUri(uri);
    return load(storage, uri, objectDefinition.getClazz(), options);
  }

  /**
   * Analyze the uri and extract the object definition alias from this. Using the {@link #objectApi}
   * identifies the {@link ObjectDefinition} belongs to the alias.
   * 
   * @param uri The uri of an object.
   * @return The {@link ObjectDefinition} or null if not found.
   */
  protected ObjectDefinition<?> getDefinitionFromUri(URI uri) {
    if (uri == null || uri.getScheme() == null || uri.getScheme().isEmpty()) {
      return null;
    }
    String scheme = uri.getScheme();
    if (scheme == null) {
      log.debug("Unable to load {} uri, the Storage not found by the scheme", uri);
      return null;
    }

    // Try to identify the ObjectDefintion by the URI
    ObjectDefinition<?> objectDefinition = objectApi.definition(uri);
    return objectDefinition;
  }

  @Override
  public <T> T read(Storage storage, URI uri, Class<T> clazz) {
    return load(storage, uri, clazz).getObject();
  }

  @Override
  public Object read(Storage storage, URI uri) {
    return load(storage, uri).getObject();
  }

  @Override
  public <T> List<T> read(Storage storage, List<URI> uris, Class<T> clazz) {
    List<StorageObject<T>> load = load(storage, uris, clazz);
    return load.stream().map(s -> s.getObject()).filter(o -> o != null)
        .collect(Collectors.toList());
  }

  /**
   * We have this constructor method to avoid having public setters in the {@link StorageObject}.
   * This can be used by the implementations of the {@link ObjectStorage}.
   * 
   * @param <T>
   * @param storage
   * @param objectDefinition
   * @param object
   * @param data
   * @return
   */
  protected <T> StorageObject<T> instanceOf(Storage storage, ObjectDefinition<T> objectDefinition,
      T object, StorageObjectData data) {
    StorageObject<T> storageObject = new StorageObject<>(objectDefinition, storage);
    storageObject.setObjectInner(object);
    storageObject.setVersion(data.getCurrentVersion());
    return storageObject;
  }

  /**
   * We have this constructor method to avoid having public setters in the {@link StorageObject}.
   * This can be used by the implementations of the {@link ObjectStorage}.
   * 
   * @param <T>
   * @param storage
   * @param objectDefinition
   * @param objectUri
   * @param data
   * @return
   */
  protected <T> StorageObject<T> instanceOf(Storage storage, ObjectDefinition<T> objectDefinition,
      URI objectUri, StorageObjectData data) {
    StorageObject<T> storageObject = new StorageObject<>(objectDefinition, storage);
    storageObject.setUri(objectUri);
    try {
      storageObject.setUuid(UUID.fromString(PathUtility.getLastPath(objectUri.getPath())));
    } catch (Exception e) {
      // do nothing
    }
    storageObject.setVersion(data.getCurrentVersion());
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
   * @param relationData The {@link StorageObjectRelationData}
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
    if (storageObjectRelationData == null) {
      return;
    }
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

  /**
   * Analyze the uri and the storageObj. If the uri refers to a given version then it returns the
   * given version else the {@link StorageObjectData#getCurrentVersion()}. The version identifies
   * the object version {@link ObjectVersion#getSerialNoData()}! It's not related with the versions
   * of the relations!
   * 
   * @param uri The uri.
   * @param storageObjData The storageObject data bean.
   * @return The related {@link ObjectVersion} from the storage object.
   */
  protected final Long getVersionByUri(URI uri, StorageObjectData storageObjData) {
    String versionSerialNoString = uri.getFragment();
    if (versionSerialNoString != null) {
      try {
        return Long.valueOf(versionSerialNoString);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Bad version format in " + uri + " object uri.", e);
      }
    }
    return storageObjData.getCurrentVersion().getSerialNoData();
  }

  protected void invokeOnSucceedFunctions(StorageObject<?> object,
      StorageSaveEvent storageSaveEvent) {
    if (onSucceedListeners != null) {
      onSucceedListeners.forEach(l -> l.doOnSave(storageSaveEvent));
    }
    object.invokeOnSucceedFunctions(storageSaveEvent);
  }

  /**
   * This supplier is responsible for accessing the current user in the actual context.
   * 
   * @param versionCreatedBy
   */
  public final ObjectStorage currentUserSupplier(Supplier<String> versionCreatedBy) {
    this.versionCreatedBy = versionCreatedBy;
    return this;
  }

  protected void updateStorageObjectWithVersion(StorageObject<?> storageObject,
      ObjectVersion version) {
    storageObject.setVersion(version);
  }

}
