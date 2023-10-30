package org.smartbit4all.api.object;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.RetrievalMode;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ReferenceDefinition;
import com.google.common.base.Strings;

/**
 * The retrieval request that defines the object node of the request.
 * 
 * @author Peter Boros
 */
public final class RetrievalRequest {

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
  private final Map<ReferenceDefinition, RetrievalRequest> references = new HashMap<>();

  /**
   * The predecessor that is the previous request node in the path. We use it during the build to
   * access the this request node and continue from there.
   */
  private WeakReference<RetrievalRequest> predecessor;

  /**
   * If we want to remember a specific node it will be saved into this variable.
   */
  private WeakReference<RetrievalRequest> lastSavePoint;

  private WeakReference<ObjectApi> objectApi;

  private final RetrievalMode retrievalMode;

  /**
   * If we have a recursive request then this is the starting point of the recursion. It acts like a
   * label to go back when we reach the same object node later on. The label is a logical jump point
   * in the {@link RetrievalRequest} that is a definition of the retrieval operation. This label
   * should be unique in a path.
   */
  private String recursiveStartLabel;

  /**
   * We can setup the {@link #recursiveStartLabel} to continue at. If we have a leaf node then the
   * retrieval operation can be continued from the same point.
   */
  private WeakReference<RetrievalRequest> continueRecursionAt;

  /**
   * The request is constructed by the {@link ObjectApi} with an {@link ObjectDefinition}.
   * 
   */
  public RetrievalRequest(ObjectApi objectApi, ObjectDefinition<?> definition,
      RetrievalMode retrievalMode) {
    this.definition = definition;
    predecessor = null;
    lastSavePoint = null;
    this.objectApi = new WeakReference<>(objectApi);
    this.retrievalMode = retrievalMode;
  }

  /**
   * The object request is constructed by itself and the RetrievalRequest.
   * 
   */
  RetrievalRequest(ObjectDefinition<?> definition, RetrievalRequest predecessor) {
    this.definition = definition;
    // It will be the predecessor and its last save point is copied.
    this.predecessor = new WeakReference<>(predecessor);
    this.lastSavePoint = predecessor.lastSavePoint;
    this.objectApi = predecessor.objectApi;
    this.retrievalMode = predecessor.retrievalMode;
  }

  /**
   * @return The object definition of the given object.
   */
  public final ObjectDefinition<?> getDefinition() {
    return definition;
  }

  /**
   * @return The Map of the referred {@link RetrievalRequest}. They are mapped by the name of the
   *         outgoing reference we can access them through.
   */
  public final Map<ReferenceDefinition, RetrievalRequest> getReferences() {
    return references;
  }

  /**
   * This function adds a new Object to the request by the given {@link ReferenceDefinition}.
   * 
   * @param paths The names of the outgoing references to follow when loading the referred objects.
   *        Without any modification it is the name of the property that contains the referrer URI.
   * @return The {@link RetrievalRequest} of the last referred object. If paths is null or empty,
   *         returns this.
   */
  public final RetrievalRequest append(String... paths) {
    return getOrCreate(true, new HashMap<>(), paths);
  }

  /**
   * Similar to {@link #append(String...)}, but return this, the root request, so subsequent
   * <code>add</code> calls will add paths to the same request.
   * 
   * @param paths The names of the outgoing references to follow when loading the referred objects.
   *        Without any modification it is the name of the property that contains the referrer URI.
   * @return
   */
  public final RetrievalRequest add(String... paths) {
    append(paths);
    return this;
  }

  /**
   * Similar to {@link #append(String...)}, but return this, the root request, so subsequent
   * <code>add</code> calls will add paths to the same request. The request node is added as
   * recursive label. Later on it can be referred as continue at point.
   *
   * @param label The label of the recursive starting point.
   * @param paths The names of the outgoing references to follow when loading the referred objects.
   *        Without any modification it is the name of the property that contains the referrer URI.
   * @return
   */
  public final RetrievalRequest addRecursiveLabel(String label, String... paths) {
    RetrievalRequest append = getOrCreate(true, new HashMap<>(), paths);
    append.recursiveStartLabel = label;
    return this;
  }

  /**
   * Similar to {@link #append(String...)}, but return this, the root request, so subsequent
   * <code>add</code> calls will add paths to the same request. The request node is added with
   * continue at label refers to the continue at point.
   *
   * @param label The label of the recursive starting point.
   * @param paths The names of the outgoing references to follow when loading the referred objects.
   *        Without any modification it is the name of the property that contains the referrer URI.
   * @return
   */
  public final RetrievalRequest addContinueAt(String label, String... paths) {
    HashMap<String, RetrievalRequest> recursiveLabels = new HashMap<>();
    RetrievalRequest append = getOrCreate(true, recursiveLabels, paths);
    RetrievalRequest continueAt = recursiveLabels.get(label);
    if (continueAt == null) {
      throw new IllegalArgumentException("The unable to find the continue at point: " + label);
    }
    append.continueRecursionAt = new WeakReference<>(continueAt);
    return this;
  }

  /**
   * Return request on given path. If request is missing anywhere in path, returns null.
   * 
   * @param paths
   * @return
   */
  public final RetrievalRequest get(String... paths) {
    return getOrCreate(false, new HashMap<>(), paths);
  }

  /**
   * Traverses this request by the path, and if needed, creates next request.
   * 
   * @param create
   * @param paths
   * @return
   */
  private final RetrievalRequest getOrCreate(boolean create,
      Map<String, RetrievalRequest> recursiveLabels, String... paths) {
    if (paths == null || paths.length == 0) {
      return this;
    }
    if (recursiveStartLabel != null) {
      recursiveLabels.put(recursiveStartLabel, this);
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
    RetrievalRequest next = references.get(ref);
    if (next == null && !create) {
      return null;
    }
    if (next == null) {
      next = new RetrievalRequest(ref.getTarget(), this);
      boolean nextLoadLatest = calcLoadLatest(ref, retrievalMode);
      next.setLoadLatest(nextLoadLatest);
      references.put(ref, next);
    }
    String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
    return next.getOrCreate(create, recursiveLabels, subPaths);
  }

  public static boolean calcLoadLatest(ReferenceDefinition ref, RetrievalMode retrievalMode) {
    return retrievalMode == RetrievalMode.LATEST
        || (retrievalMode == RetrievalMode.NORMAL
            && ref.getAggregation() == AggregationKind.NONE)
        || (retrievalMode == RetrievalMode.NORMAL
            && ref.getAggregation() == AggregationKind.SHARED);
  }

  /**
   * A recursive function to collect all request nodes inclusively itself into a {@link Stream} for
   * further processing.
   * 
   * @return The nodes {@link Stream}.
   */
  public Stream<RetrievalRequest> all() {
    return Stream.of(
        Stream.of(this),
        references.values().stream().flatMap(RetrievalRequest::all))
        .flatMap(s -> s);
  }


  /**
   * @return The predecessor to continue the request building from.
   */
  public final RetrievalRequest pre() {
    return predecessor.get();
  }

  /**
   * @return Set the current node to last saved node to remember this when coming {@link #back()}.
   */
  public final RetrievalRequest set() {
    lastSavePoint = new WeakReference<>(this);
    return this;
  }

  /**
   * @return Go back to the last {@link #set()} point to continue building the request.
   */
  public final RetrievalRequest back() {
    return lastSavePoint.get();
  }

  /**
   * @return Go back to the root node.
   */
  public final RetrievalRequest root() {
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
  public final RetrievalRequest setLoadLatest(boolean loadLatest) {
    this.loadLatest = loadLatest;
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

  /**
   * If we have a recursive request then this is the starting point of the recursion. It acts like a
   * label to go back when we reach the same object node later on. The label is a logical jump point
   * in the {@link RetrievalRequest} that is a definition of the retrieval operation. This label
   * should be unique in a path.
   * 
   * @return
   */
  public final String getRecursiveStartLabel() {
    return recursiveStartLabel;
  }

  /**
   * We can setup the {@link #recursiveStartLabel} to continue at. If we have a leaf node then the
   * retrieval operation can be continued from the same point.
   * 
   * @return
   */
  public final RetrievalRequest getContinueRecursionAt() {
    return continueRecursionAt != null ? continueRecursionAt.get() : null;
  }

  public final RetrievalRequest recursiveStartLabel(String recursiveStartLabel) {
    this.recursiveStartLabel = recursiveStartLabel;
    return this;
  }

}
