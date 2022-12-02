package org.smartbit4all.api.object;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.core.object.ObjectDefinition;

/**
 * The change request for one object with a required operation. The object itself can be directly a
 * {@link Map} or the java object. If we set the java object then we don't use the map.
 * {@link #objectAsMap}!
 * 
 * @author Peter Boros
 */
public class ObjectChangeRequest {

  public enum ObjectChangeOperation {
    NEW, UPDATE, DELETE, NOP
  }

  /**
   * The object definition to access the meta of the given object.
   */
  private final ObjectDefinition<?> definition;

  /**
   * The logical storage scheme if it is used in the given storage implementation.
   */
  private final String storageScheme;

  /**
   * 
   */
  private ObjectChangeOperation operation;

  /**
   * For better destruction.
   */
  private WeakReference<ApplyChangeRequest> requestRef;

  private final URI changedUri;

  ObjectChangeRequest(ApplyChangeRequest request, ObjectDefinition<?> definition,
      String storageScheme,
      ObjectChangeOperation operation) {
    super();
    this.definition = definition;
    this.storageScheme = storageScheme;
    this.operation = operation;
    this.changedUri = null;
    requestRef = new WeakReference<>(request);
  }

  ObjectChangeRequest(ApplyChangeRequest request, URI changedUri,
      ObjectChangeOperation operation) {
    super();
    this.definition = null;
    this.storageScheme = null;
    this.operation = operation;
    this.changedUri = changedUri;
    requestRef = new WeakReference<>(request);
  }

  /**
   * The uri of the object to change. If it is null then the {@link #operation} must be
   * {@link ObjectChangeOperation#NEW}.
   */
  private URI uri;

  /**
   * The object values of the request. The existing properties of the map are going to set during
   * the save and the rest of the property value will remain the same. If the {@link #object} is set
   * then we don't use this map!
   */
  private Map<String, Object> objectAsMap;

  /**
   * If we have the object the {@link #objectAsMap} is not used!
   */
  private Object object;

  /**
   * The reference changes for the object.
   */
  private final Map<String, ReferenceChangeRequest> referenceChanges = new HashMap<>();

  /**
   * We can construct a new reference value change for the outgoing reference.
   * 
   * @param reference The outgoing reference.
   * @return
   */
  public ReferenceValueChange referenceValue(String reference) {
    return (ReferenceValueChange) referenceChanges.computeIfAbsent(reference,
        d -> new ReferenceValueChange(request(), this, definition.getOutgoingReference(reference)));
  }

  public ReferenceListChange referenceList(String reference) {

    return (ReferenceListChange) referenceChanges.computeIfAbsent(reference,
        d -> new ReferenceListChange(request(), this, definition.getOutgoingReference(reference)));
  }

  public final URI getUri() {
    return uri;
  }

  public final void setUri(URI uri) {
    this.uri = uri;
  }

  public final ObjectChangeRequest uri(URI uri) {
    setUri(uri);
    return this;
  }

  public final Map<String, Object> getObjectAsMap() {
    return objectAsMap;
  }

  public final Map<String, Object> getOrCreateObjectAsMap() {
    if (objectAsMap == null) {
      if (definition != null && object != null) {
        objectAsMap = definition.toMap(object);
      } else {
        objectAsMap = new HashMap<>();
      }
    }
    return objectAsMap;
  }

  public final void setObjectAsMap(Map<String, Object> objectAsMap) {
    this.objectAsMap = objectAsMap;
  }

  public final ObjectChangeRequest objectAsMap(Map<String, Object> objectAsMap) {
    setObjectAsMap(objectAsMap);
    return this;
  }

  public final Object getObject() {
    return object;
  }

  public final void setObject(Object object) {
    this.object = object;
  }

  public final ObjectChangeRequest object(Object object) {
    setObject(object);
    return this;
  }

  public final ObjectDefinition<?> getDefinition() {
    return definition;
  }

  public final ObjectChangeOperation getOperation() {
    return operation;
  }

  public final Map<String, ReferenceChangeRequest> getReferenceChanges() {
    return referenceChanges;
  }

  public final ApplyChangeRequest request() {
    return requestRef.get();
  }

  public final String getStorageScheme() {
    return storageScheme;
  }

  final void setOperation(ObjectChangeOperation operation) {
    this.operation = operation;
  }

  public URI getChangedUri() {
    return changedUri;
  }

}
