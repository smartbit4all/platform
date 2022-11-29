package org.smartbit4all.api.object;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ReferenceDefinition;
import com.google.common.base.Strings;

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
  private boolean loadLatest = false;

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

  private WeakReference<ObjectApi> objectApi;

  /**
   * The object request is constructed by the {@link RetrievalApi}.
   * 
   */
  ObjectRetrievalRequest(ObjectDefinition<?> definition, ObjectRetrievalRequest predecessor) {
    this.definition = definition;
    // It will be the predecessor and its last save point is copied.
    this.predecessor = new WeakReference<>(predecessor);
    this.lastSavePoint = predecessor.lastSavePoint;
    this.objectApi = predecessor.objectApi;
  }

  /**
   * The object request is constructed by itself and the RetrievalRequest.
   * 
   */
  public ObjectRetrievalRequest(ObjectApi objectApi, ObjectDefinition<?> definition) {
    this.definition = definition;
    predecessor = null;
    lastSavePoint = null;
    this.objectApi = new WeakReference<>(objectApi);
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
   * @param paths The names of the outgoing references to follow when loading the referred objects.
   *        Without any modification it is the name of the property that contains the referrer URI.
   * @return The {@link ObjectRetrievalRequest} of the last referred object. If paths is null or
   *         empty, returns this.
   */
  public final ObjectRetrievalRequest append(String... paths) {
    return getOrCreate(true, paths);
  }

  /**
   * Similar to {@link #append(String...)}, but return this, the root request, so subsequent
   * <code>add</code> calls will add paths to the same request.
   * 
   * @param paths
   * @return
   */
  public final ObjectRetrievalRequest add(String... paths) {
    append(paths);
    return this;
  }

  /**
   * Return request on given path. If request is missing anywhere in path, returns null.
   * 
   * @param paths
   * @return
   */
  public final ObjectRetrievalRequest get(String... paths) {
    return getOrCreate(false, paths);
  }

  /**
   * Traverses this request by the path, and if needed, creates next request.
   * 
   * @param create
   * @param paths
   * @return
   */
  private final ObjectRetrievalRequest getOrCreate(boolean create, String... paths) {
    if (paths == null || paths.length == 0) {
      return this;
    }
    String path = paths[0];
    if (Strings.isNullOrEmpty(path)) {
      throw new IllegalArgumentException("Path part cannot be null or empty");
    }
    ReferenceDefinition ref = definition.getOutgoingReference(path);
    if (ref == null) {
      throw new IllegalArgumentException(
          "The " + path + " is not an existing outgoing reference in the "
              + definition.getQualifiedName() + " object.");
    }
    ObjectRetrievalRequest next = references.get(ref);
    if (next == null && !create) {
      return null;
    }
    if (next == null) {
      next = new ObjectRetrievalRequest(ref.getTarget(), this);
      if (ref.getAggregation() == AggregationKind.NONE) {
        next.loadLatest();
      }
      references.put(ref, next);
    }
    String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
    return next.getOrCreate(create, subPaths);
  }

  /**
   * A recursive function to collect all request nodes inclusively itself into a {@link Stream} for
   * further processing.
   * 
   * @return The nodes {@link Stream}.
   */
  public Stream<ObjectRetrievalRequest> all() {
    return Stream.of(
        Stream.of(this),
        references.values().stream().flatMap(ObjectRetrievalRequest::all))
        .flatMap(s -> s);
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
  public final boolean isLoadLatest() {
    return loadLatest;
  }

  /**
   * Defined if we need the latest version of the given object. By default we load the referred
   * version directly.
   * 
   */
  public final ObjectRetrievalRequest loadLatest() {
    this.loadLatest = true;
    return this;
  }

  /**
   * Defined if we need the latest version of the given object. By default we load the referred
   * version directly.
   * 
   */
  public final ObjectRetrievalRequest setLoadLatest(boolean loadLatest) {
    this.loadLatest = loadLatest;
    return this;
  }

  /**
   * Defined if we need the latest version of the given object. By default we load the referred
   * version directly.
   * 
   */
  public final ObjectRetrievalRequest version() {
    this.loadLatest = false;
    return this;
  }

  /**
   * Load the whole request from the root, with a specified objectUri.
   * 
   * @param objectUri
   * @return
   */
  public final ObjectNode load(URI objectUri) {
    return objectApi.get().load(root(), objectUri);
  }

  /**
   * Load the whole request from the root, with a specified objectUri.
   * 
   * @param objectUris
   * @return
   */
  public List<ObjectNode> load(List<URI> objectUris) {
    return objectApi.get().load(root(), objectUris);
  }

}
