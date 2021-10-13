package org.smartbit4all.domain.data.storage;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.storage.bean.StorageObjectData;
import org.smartbit4all.core.object.ObjectDefinition;

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

  /**
   * The object reference. Can be null because if we creates a new object without existing domain
   * object then this represents the existence only.
   */
  private T object;

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
   * If it's true then the save will check if the current version matched with version is the same.
   * If it differs then the save throws an exception. We have to reload the object and try again or
   * we must setup a lock to avoid parallel modification.
   */
  private boolean strictVersionCheck = false;

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

  public final URI getUri() {
    return uri;
  }

  final void setUri(URI uri) {
    this.uri = uri;
  }

  public final UUID getUuid() {
    return uuid;
  }

  final void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public final ObjectDefinition<T> definition() {
    return definition;
  }

  public final T getObject() {
    return object;
  }

  /**
   * Used for the load to use the uri of the object itself.
   * 
   * @param object
   */
  final void setObjectInner(T object) {
    this.object = object;
    uri = definition.getUri(object);
  }

  /**
   * Set the object. The object uri will be set to the URI of this {@link StorageObject}! From that
   * point we must use this object as a contained part of the {@link StorageObject}.
   * 
   * @param object
   */
  public final void setObject(T object) {
    this.object = object;
    definition.setUri(object, uri);
  }

  /**
   * Set the object. The object uri will be set to the URI of this {@link StorageObject}! From that
   * point we must use this object as a contained part of the {@link StorageObject}.
   * 
   * @param object
   */
  @SuppressWarnings("unchecked")
  final void setObjectObj(Object object) {
    this.object = (T) object;
    definition.setUri(this.object, uri);
  }

  /**
   * @return If the object exists or not.
   */
  public boolean isPresent() {
    return object == null ? false : true;
  }

  StorageObject<T> copy() {
    StorageObject<T> result = new StorageObject<>(definition, storageRef.get());
    result.setObject(object);
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

  public final StorageObject<T> onSucceed() {
    return this;
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
   * @param <T>
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

  final void setVersion(ObjectVersion version) {
    this.version = version;
  }

  public final boolean isStrictVersionCheck() {
    return strictVersionCheck;
  }

  public final void setStrictVersionCheck(boolean strictVersionCheck) {
    this.strictVersionCheck = strictVersionCheck;
  }

}
