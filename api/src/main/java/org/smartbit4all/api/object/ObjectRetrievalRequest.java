package org.smartbit4all.api.object;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.domain.service.query.RetrievalRequest;

/**
 * The object retrieval request that defines the object node of the request.
 * 
 * @author Peter Boros
 */
public final class ObjectRetrievalRequest {

  /**
   * Defined if we need the latest version of the given object. By default we load the referred
   * version directly.
   */
  private boolean loadHead = false;

  /**
   * The object definition of the given object.
   */
  private ObjectDefinition<?> definition;

  /**
   * The objects to load trough the references. The name of the outgoing reference is the key.
   */
  private final Map<ReferenceDefinition, ObjectRetrievalRequest> references = new HashMap<>();

  /**
   * The predecessor that is the previous request node in the path. We use it during the build to
   * access the this request node and continue from there.
   */
  private WeakReference<ObjectRetrievalRequest> predecessor;

  /**
   * If we want to remember a specific node it will be saved into this variable.
   */
  private WeakReference<ObjectRetrievalRequest> lastSavePoint;

  private WeakReference<RetrievalApi> retrievalApi;

  /**
   * The object request is constructed by the {@link RetrievalApi}.
   * 
   */
  ObjectRetrievalRequest(ObjectDefinition<?> definition, ObjectRetrievalRequest predecessor) {
    this.definition = definition;
    // It will be the predecessor and its last save point is copied.
    this.predecessor = new WeakReference<>(predecessor);
    this.lastSavePoint = predecessor.lastSavePoint;
    this.loadHead = predecessor.loadHead;
    this.retrievalApi = predecessor.retrievalApi;
  }

  /**
   * The object request is constructed by itself and the {@link RetrievalRequest}.
   * 
   */
  ObjectRetrievalRequest(RetrievalApi retrievalApi, ObjectDefinition<?> definition) {
    this.definition = definition;
    this.loadHead = false;
    predecessor = null;
    lastSavePoint = null;
    this.retrievalApi = new WeakReference<>(retrievalApi);
  }

  /**
   * @return The object definition of the given object.
   */
  public final ObjectDefinition<?> getDefinition() {
    return definition;
  }

  /**
   * @return The Map of the referred {@link ObjectRetrievalRequest}. They are mapped by the name of
   *         the outgoing reference we can access them through.
   */
  public final Map<ReferenceDefinition, ObjectRetrievalRequest> getReferences() {
    return references;
  }

  /**
   * This function adds a new Object to the request by the given {@link ReferenceDefinition}.
   * 
   * @param outgoingReference The name of the outgoing reference to follow when loading the referred
   *        objects. Without any modification it is the name of the property that contains the
   *        referrer URI.
   * @return The {@link ObjectRetrievalRequest} of the referred object.
   */
  public final ObjectRetrievalRequest append(String outgoingReference) {
    ReferenceDefinition reference = definition.getOutgoingReference(outgoingReference);
    if (reference == null) {
      throw new IllegalArgumentException(
          "The " + outgoingReference + " is not an existing outgoing reference in the "
              + definition.getQualifiedName() + " object.");
    }
    return references.computeIfAbsent(reference,
        r -> new ObjectRetrievalRequest(r.getTarget(), this));
  }

  /**
   * @return The predecessor to continue the request building from.
   */
  public final ObjectRetrievalRequest pre() {
    return predecessor.get();
  }

  /**
   * @return Set the current node to last saved node to remember this when coming {@link #back()}.
   */
  public final ObjectRetrievalRequest set() {
    lastSavePoint = new WeakReference<>(this);
    return this;
  }

  /**
   * @return Go back to the last {@link #set()} point to continue building the request.
   */
  public final ObjectRetrievalRequest back() {
    return lastSavePoint.get();
  }

  /**
   * @return Go back to the root node.
   */
  public final ObjectRetrievalRequest root() {
    return predecessor == null ? this : predecessor.get().root();
  }

  /**
   * Defined if we need the latest version of the given object. By default we load the referred
   * version directly.
   * 
   * @return
   */
  public final boolean isLoadHead() {
    return loadHead;
  }

  /**
   * Defined if we need the latest version of the given object. By default we load the referred
   * version directly.
   * 
   */
  public final ObjectRetrievalRequest head() {
    this.loadHead = true;
    return this;
  }

  /**
   * Defined if we need the latest version of the given object. By default we load the referred
   * version directly.
   * 
   */
  public final ObjectRetrievalRequest version() {
    this.loadHead = false;
    return this;
  }

  /**
   * Load the whole request from the root.
   * 
   * @param uri
   * @return
   */
  public final ObjectNode load(URI uri) {
    return retrievalApi.get().load(root(), uri);
  }

}
