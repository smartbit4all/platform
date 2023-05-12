package org.smartbit4all.domain.data.storage;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.utility.UriUtils;

/**
 * The wrapper object for storage operations. It represents an object with all the belonging
 * references. It's a composed aggregate with a root object and other object contained and referred
 * by this object. This aggregate is the piece of transaction in a microservice environment. The
 * storage object always has an URI that is a logical URI that is bound to the physical location
 * with a special mapping. The URI looks like the following: scheme:/object_class/creation_time/UUID
 * This can be managed by any {@link ObjectStorage} implementation the scheme can separate the
 * different logical units, the first item in the path identifies the object type (by the class
 * name) and the rest of the path is the creation time in year/month/day/hour/min format. The final
 * item is a UUID that should be unique individually also. In a running application this URI always
 * identifies a given object.
 *
 * @author Peter Boros
 * @param <T>
 */
public final class StorageObject<T> {

  /**
   * The URI of the object. The URI looks like the following:
   * scheme:/object_class/creation_time/UUID This can be managed by any {@link ObjectStorage}
   * implementation the scheme can separate the different logical units, the first item in the path
   * identifies the object type (by the class name) and the rest of the path is the creation time in
   * year/month/day/hour/min format. The final item is a UUID that should be unique individually
   * also.
   */
  private URI uri;

  /**
   * The uuid part of the uri as redundant field
   */
  private UUID uuid;

  /**
   * The {@link ObjectDefinition} of the
   */
  private final ObjectDefinition<T> definition;

  /**
   * The {@link StorageObject} is a piece of transaction over the {@link StorageApi}. Therefore it
   * will have a unique identifier for this transaction.
   */
  private final UUID transactionId = UUID.randomUUID();

  public enum OperationMode {
    AS_OBJECT, AS_MAP
  }

  /**
   * The storage object has two different operation mode depending on the priority of the data
   * representations. The {@link OperationMode#AS_OBJECT} means that the primary data representation
   * is the {@link #object} variable. So before the persisting the object then we have to update the
   * map.
   */
  private OperationMode mode = OperationMode.AS_OBJECT;

  /**
   * The object reference. Can be null because if we creates a new object without existing domain
   * object then this represents the existence only.
   */
  private T object;

  /**
   * The storage object use this map as the basic data. The {@link #object} is constructed on demand
   * when someone call the {@link #getObject()} function. If we change the {@link #objectAsMap} the
   * we delete the {@link #object} to reconstruct it during the subsequent {@link #getObject()}
   * call.
   */
  private Map<String, Object> objectAsMap;

  /**
   * The reference for the storage that is the logical schema for this object. All the related
   * objects will be stored in this schema if that are attached to this object by reference or added
   * to a collection. It's {@link WeakReference} to make it easier to free the object as a memory
   * construction.
   */
  private final WeakReference<Storage> storageRef;

  /**
   * The version of the object. If it's null then we don't know the version because it can be a new
   */
  private ObjectVersion version;

  /**
   * The lock belongs to this {@link StorageObject}.
   *
   */
  private StorageObjectLock lock;

  /**
   * These are the version policies that can be used in the storage object to instruct the object
   * storage about the new and new versions of an object.
   *
   * @author Peter Boros
   */
  public enum VersionPolicy {
    /**
     * In this case all the saves results new and new versions about the given object regardless of
     * if it was really changed or not.
     */
    ALLVERSION,
    /**
     * The save is going to compare the new version with the currently active one. If there is no
     * difference then it will skip the creation of the new version. TODO Not implemented yet.
     */
    MERGESAMEVERSIONS,
    /**
     * In this case there will be only one version from a given object. This policy can be used to
     * optimize the usage of control objects where the versions are not really useful.
     */
    SINGLEVERSION
  }

  /**
   * If it's true then the save will check if the current version matched with version is the same.
   * If it differs then the save throws an exception. We have to reload the object and try again or
   * we must setup a lock to avoid parallel modification.
   */
  private boolean strictVersionCheck = false;

  /**
   * The storage object operation gives instructions to object storage about the given object.
   *
   * @author Peter Boros
   */
  public enum StorageObjectOperation {

    CREATE, MODIFY, MODIFY_WITHOUT_DATA, DELETE

  }

  /**
   * State shows the current operation about storage object.
   */
  private StorageObjectOperation operation = StorageObjectOperation.CREATE;

  /**
   * The references.
   */
  private Map<String, StorageObjectReferenceEntry> references = new HashMap<>();

  /**
   * The collections by name and the entries by identifier.
   */
  private Map<String, Map<String, StorageObjectReferenceEntry>> collections = new HashMap<>();

  /**
   * The onSucceedFunctionList list is the ordered collection of the functions that should be
   * executed when the {@link Storage#save(StorageObject)} is successfully finished.
   *
   *
   * TODO Detailed specification.
   */
  private List<Consumer<StorageSaveEvent>> onSucceedFunctionList = null;

  private boolean skipLock = false;

  /**
   * The Storage cann't be created directly! Use the Storage that would manage this object to have a
   * new one by {@link Storage#instanceOf(Class)} or load one by
   * {@link Storage#load(URI, Class, StorageLoadOption...)}
   *
   * @param objectDefinition The {@link ObjectDefinition} of the class that is wrapped by this
   *        {@link StorageObject}.
   * @param storage The storage itself.
   */
  StorageObject(ObjectDefinition<T> objectDefinition, Storage storage) {
    this.definition = objectDefinition;
    this.storageRef = new WeakReference<>(storage);
  }

  /**
   * @return The URI of the object {@link #uri}.
   */
  public final URI getUri() {
    return uri;
  }

  /**
   * The URI of the object {@link #uri}.
   *
   * @param uri
   */
  final void setUri(URI uri) {
    this.uri = uri;
  }

  /**
   * @return The {@link #uuid} of the object.
   */
  public final UUID getUuid() {
    return uuid;
  }

  /**
   * @param uuid The {@link #uuid} of the object.
   */
  final void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public final ObjectDefinition<T> definition() {
    return definition;
  }

  /**
   * @deprecated We plan to remove this function, because AS_OBJECT mode will be removed to prevent
   *             accidental overwriting of extra information stored in ObjectMap.
   * @return
   */
  @Deprecated
  public final T getObject() {
    if (object == null) {
      object = definition().fromMap(objectAsMap);
    }
    return object;
  }

  /**
   * Used for the load to use the uri of the object itself. Remove the history (fragment) part of
   * the URI.
   *
   * @param object
   */
  final void setObjectInner(T object) {
    this.object = object;
    uri = UriUtils.removeFragment(definition.getUri(object));
  }

  /**
   * Set the object. The object uri will be set to the URI of this {@link StorageObject}! From that
   * point we must use this object as a contained part of the {@link StorageObject}.
   *
   * @param object
   */
  public final StorageObject<T> setObject(T object) {
    this.object = object;
    if (definition.isExplicitUri()) {
      uri = definition.getUri(object);
    } else {
      definition.setUri(object, uri);
    }
    // We must update the map to contain the actual values if the mode is AS_MAP.
    if (mode == OperationMode.AS_MAP) {
      updateObjectMap(object);
    }
    return this;
  }

  private Map<String, Object> updateObjectMap(Object object) {
    if (object == null) {
      return objectAsMap;
    }
    Map<String, Object> map = definition.toMap(object);
    if (objectAsMap != null) {
      // TODO Recursive putall for the contained object.
      objectAsMap.putAll(map);
    } else {
      objectAsMap = map;
    }
    return objectAsMap;
  }

  /**
   * Set the object. The object uri will be set to the URI of this {@link StorageObject}! From that
   * point we must use this object as a contained part of the {@link StorageObject}.
   *
   * @param object
   */
  @SuppressWarnings("unchecked")
  public final void setObjectObj(Object object) {
    this.object = (T) object;
    definition.setUri(this.object, uri);
    updateObjectMap(object);
  }

  /**
   * @return If the object exists or not.
   */
  public boolean isPresent() {
    return objectAsMap == null ? false : true;
  }

  StorageObject<T> copy() {
    StorageObject<T> result = new StorageObject<>(definition, storageRef.get());
    if (mode == OperationMode.AS_MAP) {
      result.setObjectAsMap(objectAsMap);
    } else {
      result.setObject(object);
    }
    result.mode = mode;
    result.setUri(uri);
    result.setUuid(uuid);
    result.setVersion(version);
    // Deep copy of the relations
    result.collections =
        collections != null
            ? collections.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                    e -> e.getValue().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, re -> {
                          return new StorageObjectReferenceEntry(re.getValue());
                        }))))
            : null;
    result.references = references != null
        ? references.entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, e -> new StorageObjectReferenceEntry(e.getValue())))
        : null;
    return result;
  }

  public final UUID getTransactionId() {
    return transactionId;
  }

  public final void setDeleted() {
    operation = StorageObjectOperation.DELETE;
  }

  /**
   * The reference will be saved as the reference describes.
   *
   * @param referenceName The name of the reference.
   * @param reference The reference data containing the URI and some other information.
   */
  public final void setReference(String referenceName, ObjectReference reference) {
    references.put(referenceName, new StorageObjectReferenceEntry(reference));
  }

  /**
   * @param referenceName The name of the reference.
   * @param containedObject The object itself that will be saved by the storage that we are working
   *        with currently.
   */
  public final void setReference(String referenceName, Object containedObject) {
    references.put(referenceName,
        containedObject != null
            ? new StorageObjectReferenceEntry(containedObject, containedObject.getClass())
            : new StorageObjectReferenceEntry());
  }

  public final StorageObjectReferenceEntry getReference(String referenceName) {
    return references.get(referenceName);
  }

  /**
   * Clear the given reference.
   *
   * @param referenceName
   */
  public final void clearReference(String referenceName) {
    references.put(referenceName, new StorageObjectReferenceEntry());
  }

  protected final Map<String, StorageObjectReferenceEntry> getReferences() {
    return references;
  }

  void setReferences(Map<String, StorageObjectReferenceEntry> references) {
    this.references = references;
  }

  /**
   * The collection map where the entries are identified by the
   * {@link ObjectReference#getReferenceId()}.
   *
   * @param collectionName The name of the collection.
   * @return The map of the collection entries identified by id.
   */
  public final Map<String, StorageObjectReferenceEntry> collectionOf(String collectionName) {
    Map<String, StorageObjectReferenceEntry> result =
        collections.computeIfAbsent(collectionName, n -> new HashMap<>());
    return result;
  }

  /**
   * The collection map where the entries are identified by the
   * {@link ObjectReference#getReferenceId()}.
   *
   * @param collectionName The name of the collection.
   * @return The map of the collection entries identified by id.
   */
  public final List<StorageObjectReferenceEntry> getCollection(String collectionName) {
    Map<String, StorageObjectReferenceEntry> result =
        collections.get(collectionName);
    return result != null
        ? result.values().stream().filter(e -> !e.isDelete()).collect(Collectors.toList())
        : Collections.emptyList();
  }

  public final void addCollectionEntry(String collectionName, ObjectReference entry) {
    // In a collection we must have the ObjectReference in advance to have a unique identifier in
    // the collection.
    Map<String, StorageObjectReferenceEntry> collectionOf = collectionOf(collectionName);
    collectionOf.put(entry.getReferenceId(), new StorageObjectReferenceEntry(entry));
  }

  protected final Map<String, Map<String, StorageObjectReferenceEntry>> getCollections() {
    return collections;
  }

  void setCollections(Map<String, Map<String, StorageObjectReferenceEntry>> collections) {
    this.collections = collections;
  }

  public final StorageObjectOperation getOperation() {
    return operation;
  }

  final void setOperation(StorageObjectOperation operation) {
    this.operation = operation;
  }

  /**
   * The Storage (the logical schema) that is responsible for the given object.
   *
   * @return
   */
  public final Storage getStorage() {
    return storageRef.get();
  }

  /**
   * The version of the {@link StorageObject} that was the
   * {@link StorageObjectData#getCurrentVersion()} when we loaded the object. This version can be
   * used to manage the optimistic lock for example. During the save we can check if we have the
   * same current version. Else the object has been changed in the meantime.
   *
   * @return
   */
  public final ObjectVersion getVersion() {
    return version;
  }

  /**
   * @return Returns the version uri of the given object
   */
  public final URI getVersionUri() {
    Long uriVersion = ObjectStorageImpl.getUriVersion(uri);
    return version != null && uriVersion == null
        ? URI.create(uri.toString() + ObjectStorageImpl.versionPostfix + version.getSerialNoData())
        : uri;
  }

  final void setVersion(ObjectVersion version) {
    this.version = version;
  }

  public final boolean isStrictVersionCheck() {
    return strictVersionCheck;
  }

  public final void setStrictVersionCheck(boolean strictVersionCheck) {
    this.strictVersionCheck = strictVersionCheck;
  }

  /**
   * This function can register a function that should be executed safely after the successful save
   * operation. The function is going to be executed when before the commit point of the save. The
   * {@link StorageSaveEvent} list will be produced and saved and all these requests will be
   * executed by the InvocationApi. If we miss executing any of them then later on another node can
   * catch these requests check the validity and execute them.
   *
   * @param func The function to call
   * @return
   */
  public final StorageObject<T> onSucceed(Consumer<StorageSaveEvent> func) {
    if (onSucceedFunctionList == null) {
      onSucceedFunctionList = new ArrayList<>();
    }
    onSucceedFunctionList.add(func);
    return this;
  }

  final void invokeOnSucceedFunctions(StorageSaveEvent storageSaveEvent) {
    if (onSucceedFunctionList != null) {
      for (Consumer<StorageSaveEvent> function : onSucceedFunctionList) {
        function.accept(storageSaveEvent);
      }
    }
  }

  final StorageObjectLock getLock() {
    return lock;
  }

  final void setLock(StorageObjectLock lock) {
    this.lock = lock;
  }

  /**
   * @return The serialized form of the object by the {@link ObjectDefinition} defined.
   */
  public final BinaryData serialize() {
    return definition.serialize(getObject());
  }

  public final boolean isSkipLock() {
    return skipLock;
  }

  final void setSkipLock(boolean skipLock) {
    this.skipLock = skipLock;
  }

  /**
   * @return The object map that contains all the properties of the given object.
   */
  public final Map<String, Object> getObjectAsMap() {
    return mode == OperationMode.AS_OBJECT ? updateObjectMap(object) : objectAsMap;
  }

  public final void setObjectAsMap(Map<String, Object> objectAsMap) {
    this.objectAsMap = objectAsMap;
    if (definition.isExplicitUri()) {
      Object objUri = objectAsMap.get("uri");
      if (objUri instanceof String) {
        objUri = URI.create((String) objUri);
        objectAsMap.put("uri", objUri);
      }
      uri = (URI) objUri;
    } else {
      objectAsMap.put("uri", uri);
    }
  }

  /**
   * Used for the load to use the uri of the object itself. Remove the history (fragment) part of
   * the URI.
   *
   */
  final void setObjectAsMapInner(Map<String, Object> objectAsMap) {
    this.objectAsMap = objectAsMap;
    Object objUri = objectAsMap.get("uri");
    if (objUri instanceof String) {
      objUri = URI.create((String) objUri);
      objectAsMap.put("uri", objUri);
    }
    uri = ObjectStorageImpl.getUriWithoutVersion((URI) objUri);
  }

  /**
   * @see #mode
   * @return
   */
  public final OperationMode getMode() {
    return mode;
  }

  /**
   *
   *
   * @see #mode
   */
  public final StorageObject<T> asObject() {
    this.mode = OperationMode.AS_OBJECT;
    return this;
  }

  /**
   * @see #mode
   */
  public final StorageObject<T> asMap() {
    this.mode = OperationMode.AS_MAP;
    return this;
  }

}
