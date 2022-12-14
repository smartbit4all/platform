package org.smartbit4all.api.object;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeReference;

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
  private final WeakReference<ApplyChangeRequest> requestRef;

  /**
   * If this change request refers to an ObjectNode, we need a reference to write back result URI.
   */
  private WeakReference<ObjectNode> objectNodeRef;

  private WeakReference<ObjectNodeReference> referenceRef;

  private final URI uriToSave;

  ObjectChangeRequest(ApplyChangeRequest request, ObjectNode node) {
    this(request, node.getDefinition(), node.getStorageScheme(), opByState(node.getState()));
    this.uri = node.getObjectUri();
    this.objectAsMap = node.getObjectAsMap();
    objectNodeRef = new WeakReference<>(node);
  }

  ObjectChangeRequest(ApplyChangeRequest request, ObjectDefinition<?> definition,
      String storageScheme,
      ObjectChangeOperation operation) {
    super();
    this.definition = definition;
    this.storageScheme = storageScheme;
    this.operation = operation;
    this.uriToSave = null;
    requestRef = new WeakReference<>(request);
  }

  ObjectChangeRequest(ApplyChangeRequest request, ObjectNodeReference ref) {
    super();
    this.definition = null;
    this.storageScheme = null;
    this.operation = opByState(ref.getState());
    this.uriToSave = ref.getObjectUri();
    requestRef = new WeakReference<>(request);
    referenceRef = new WeakReference<>(ref);
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

  /**
   * This Uri has been set from outside as the Uri to save.
   * 
   * @return
   */
  public URI getUriToSaveUri() {
    return uriToSave;
  }

  private static ObjectChangeOperation opByState(ObjectNodeState state) {
    ObjectChangeOperation op;
    if (state == ObjectNodeState.NEW) {
      op = ObjectChangeOperation.NEW;
    } else if (state == ObjectNodeState.REMOVED) {
      op = ObjectChangeOperation.DELETE;
    } else if (state == ObjectNodeState.MODIFIED) {
      op = ObjectChangeOperation.UPDATE;
    } else {
      op = ObjectChangeOperation.NOP;
    }
    return op;
  }

  public void setResult(URI resultUri) {
    if (objectNodeRef != null) {
      ObjectNode node = objectNodeRef.get();
      if (node != null) {
        node.setResult(resultUri);
      }
    }
    if (referenceRef != null) {
      ObjectNodeReference ref = referenceRef.get();
      if (ref != null) {
        ref.setResult(resultUri);
      }
    }
  }

}
