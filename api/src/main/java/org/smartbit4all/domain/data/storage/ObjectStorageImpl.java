package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.Collections;
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
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
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
   * The postfix of the URI in case of version reference followed by a serial number of the version.
   */
  public static final String versionPostfix = ".v";

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

  /**
   * The extension point for the given {@link ObjectStorage} implementation to add a supplier
   * function for getting lock.
   *
   * @param objectUri
   *
   * @return The supplier
   */
  protected Supplier<StorageObjectPhysicalLock> physicalLockSupplier(URI objectUri) {
    return null;
  }

  /**
   * The extension point for the given {@link ObjectStorage} implementation to add a release
   * function to free lock.
   *
   * @return The consumer
   */
  protected Consumer<StorageObjectPhysicalLock> physicalLockReleaser() {
    return null;
  }

  /**
   * The object API gives access to the meta data of the objects.
   */
  protected final ObjectDefinitionApi objectDefinitionApi;

  protected Supplier<String> versionCreatedBy = () -> StringConstant.UNKNOWN;

  protected boolean defaultStorage = false;

  @Autowired(required = false)
  private List<ObjectStorageSaveSucceedListener> onSucceedListeners;

  protected ObjectStorageImpl(ObjectDefinitionApi objectDefinitionApi) {
    super();
    this.objectDefinitionApi = objectDefinitionApi;
  }

  @Override
  public StorageObjectLock getLock(URI objectUri) {
    objectUri = getUriWithoutVersion(objectUri);
    boolean tryAgain = true;
    while (tryAgain) {
      tryAgain = false;
      lockMutex.lock();
      try {
        StorageObjectLockEntry entry = locks.get(objectUri);
        if (entry == null) {
          final StorageObjectLockEntry newEntry =
              new StorageObjectLockEntry(objectUri, physicalLockSupplier(objectUri),
                  physicalLockReleaser());
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
      } catch (StorageObjectLockEntryRemovingException e) {
        tryAgain = true;
      } catch (InterruptedException e) {
        throw new IllegalStateException("Unable to get lock, the thread was interrupted.", e);
      } finally {
        lockMutex.unlock();
      }
    }
    return null;
  }

  @Override
  public <T> List<StorageObject<T>> load(Storage storage, List<URI> uris, Class<T> clazz,
      StorageLoadOption... options) {
    // TODO The same thread locks must be used and acquired by all the threads.
    return uris.parallelStream().map(u -> load(storage, u, clazz, options))
        .collect(Collectors.toList());
  }

  @Override
  public StorageObject<?> load(Storage storage, URI uri, StorageLoadOption... options) {
    return load(storage, uri, null, options);
  }

  /**
   * Analyze the uri and the {@link StorageObjectData} to extract the object definition from the
   * alias or from the {@link StorageObjectData}. Using the {@link #objectDefinitionApi} identifies
   * the {@link ObjectDefinition} belongs to the given class. This function is called when the
   * {@link Class} is not defined for the {@link StorageObject}.
   *
   * @param uri The uri of an object.
   * @param objectData The {@link StorageObjectData}.
   * @return The {@link ObjectDefinition} or null if not found.
   */
  protected ObjectDefinition<?> getObjectDefinition(URI uri, StorageObjectData objectData,
      Class<?> clazz) {
    if (clazz != null) {
      return objectDefinitionApi.definition(clazz);
    }
    if (objectData != null && objectData.getClassName() != null) {
      return objectDefinitionApi.definition(objectData.getClassName());
    }
    if (uri == null || uri.getScheme() == null || uri.getScheme().isEmpty()) {
      return null;
    }
    String scheme = uri.getScheme();
    if (scheme == null) {
      log.debug("Unable to load {} uri, the Storage not found by the scheme", uri);
      return null;
    }

    // Try to identify the ObjectDefintion by the URI
    ObjectDefinition<?> objectDefinition = objectDefinitionApi.definition(uri);
    if (objectDefinition == null) {
      throw new ObjectNotFoundException(uri, clazz, "Unable to retrieve object definition.");
    }
    return objectDefinition;
  }

  protected String getStorageScheme(Storage storage) {
    return storage.getScheme();
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

  @Override
  public <T> List<T> readAll(Storage storage, String setName, Class<T> clazz) {
    // By default it won't return anything. The management of the set is an extra functionality.
    return Collections.emptyList();
  }

  @Override
  public <T> List<URI> readAllUris(Storage storage, String setName, Class<T> clazz) {
    // By default it won't return anything. The management of the set is an extra functionality.
    return Collections.emptyList();
  }

  @Override
  public boolean move(URI uri, URI targetUri) {
    // By default it won't return anything. The management of the set is an extra functionality.
    return false;
  }

  @Override
  public boolean delete(URI uri) {
    // By default it won't return anything. The management of the set is an extra functionality.
    return false;
  }

  /**
   * We have this constructor method to avoid having public setters in the {@link StorageObject}.
   * This can be used by the implementations of the {@link ObjectStorage}.
   *
   * @param <T>
   * @param storage
   * @param objectDefinition
   * @param object
   * @return
   */
  protected <T> StorageObject<T> instanceOf(Storage storage, ObjectDefinition<T> objectDefinition,
      Map<String, Object> object, ObjectVersion objectVersion) {
    StorageObject<T> storageObject = new StorageObject<>(objectDefinition, storage);
    storageObject.setObjectAsMapInner(object);
    storageObject.setVersion(objectVersion);
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
    Long uriVersion = getUriVersion(uri);
    return uriVersion == null ? storageObjData.getCurrentVersion().getSerialNoData() : uriVersion;
  }

  public static final Long getUriVersion(URI uri) {
    // if (uri == null) {
    // return null;
    // }
    String path = uri.getPath();
    int idxVersionPostfix = path.lastIndexOf(versionPostfix);
    if (idxVersionPostfix >= 0) {
      String version = path.substring(idxVersionPostfix + versionPostfix.length());
      try {
        return Long.valueOf(version);
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }

  public static final String getUriId(URI uri) {
    URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(uri);
    String path = uriWithoutVersion.getPath();
    int idxIdPostfix = path.lastIndexOf("/");
    if (idxIdPostfix >= 0) {
      return path.substring(idxIdPostfix + 1);
    }
    return null;
  }

  public static final URI getUriWithoutVersion(URI uri) {
    if (uri == null) {
      return null;
    }
    String path = uri.getPath();
    String fragment = uri.getFragment();
    int idxVersionPostfix = path.lastIndexOf(versionPostfix);
    if (idxVersionPostfix >= 0) {
      return UriUtils.createUri(uri.getScheme(), null, path.substring(0, idxVersionPostfix),
          fragment);
    }
    return uri;
  }

  public static final URI getUriWithVersion(URI uri, long versionNumber) {
    URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(uri);
    return URI
        .create(uriWithoutVersion.toString() + ObjectStorageImpl.versionPostfix + versionNumber);

  }

  protected void invokeOnSucceedFunctions(StorageObject<?> object,
      StorageSaveEvent storageSaveEvent) {
    if (onSucceedListeners != null && storageSaveEvent.getNewVersion() != null) {
      for (ObjectStorageSaveSucceedListener succeedListener : onSucceedListeners) {
        String scheme = object.getStorage().getScheme();
        if (succeedListener.supportsType(storageSaveEvent.getNewVersion().getClass())
            && succeedListener.supportsSchema(scheme)) {
          succeedListener.doOnSave(storageSaveEvent);
        }
      }
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

  @Override
  public ObjectHistoryIterator objectHistory(URI uri, ObjectDefinition<?> definition) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ObjectHistoryIterator objectHistoryReverse(URI uri, ObjectDefinition<?> definition) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long lastModified(URI uri) {
    return System.currentTimeMillis();
  }

}
