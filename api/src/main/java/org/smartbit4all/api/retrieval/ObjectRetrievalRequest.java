package org.smartbit4all.api.retrieval;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * The object retrieval request that defines the object node of the request.
 * 
 * @author Peter Boros
 */
public final class ObjectRetrievalRequest {

  /**
   * The URI of the objects to load by default. It filters the given object and limits the result to
   * this list.
   */
  private final List<URI> uriList = new ArrayList<>();

  /**
   * Defined if we need the latest version of the given object. By default we load the referred
   * version directly.
   */
  private boolean loadLatestVersion = false;

  /**
   * The object definition of the given object.
   */
  private ObjectDefinition<?> definition;

  /**
   * The objects to load trough the references. The {@link ReferenceDefinition} is the key.
   */
  private final Map<ReferenceDefinition, ObjectRetrievalRequest> references = new HashMap<>();

  /**
   * The object request is constructed by itself and the {@link RetrievalRequest}.
   * 
   * @param definition
   */
  ObjectRetrievalRequest(ObjectDefinition<?> definition, boolean loadLatestVersion) {
    super();
    this.definition = definition;
    this.loadLatestVersion = loadLatestVersion;
  }

  /**
   * @return The object definition of the given object.
   */
  public final ObjectDefinition<?> getDefinition() {
    return definition;
  }

  /**
   * @return The Map of the referred {@link ObjectRetrievalRequest}. They are mapped by the
   *         {@link ReferenceDefinition} we can access them through.
   */
  public final Map<ReferenceDefinition, ObjectRetrievalRequest> getReferences() {
    return references;
  }

  /**
   * @return The list of URI that are the filters of the given object node. These are the possible
   *         object that can be retrieved in the result {@link ObjectModel}.
   */
  public final List<URI> getUriList() {
    return uriList;
  }

  /**
   * This function adds a new Object to the request by the given {@link ReferenceDefinition}.
   * 
   * @param referenceDefinition The {@link ReferenceDefinition} to follow when loading the referred
   *        objects.
   * @return The {@link ObjectRetrievalRequest} of the referred object.
   */
  public final ObjectRetrievalRequest loadBy(ReferenceDefinition referenceDefinition,
      boolean loadLatestVersion) {
    if (referenceDefinition == null) {
      return null;
    }
    return references.computeIfAbsent(referenceDefinition,
        r -> new ObjectRetrievalRequest(r.getTarget(), loadLatestVersion));
  }

  /**
   * This function adds a new Object to the request by the given {@link ReferenceDefinition}.
   * 
   * @param referenceDefinition The {@link ReferenceDefinition} to follow when loading the referred
   *        objects.
   * @return The {@link ObjectRetrievalRequest} of the referred object.
   */
  public final ObjectRetrievalRequest loadBy(ReferenceDefinition referenceDefinition) {
    return loadBy(referenceDefinition, false);
  }

  /**
   * Defined if we need the latest version of the given object. By default we load the referred
   * version directly.
   * 
   * @return
   */
  public final boolean isLoadLatestVersion() {
    return loadLatestVersion;
  }

  /**
   * Defined if we need the latest version of the given object. By default we load the referred
   * version directly.
   * 
   * @param loadLatestVersion
   */
  public final void setLoadLatestVersion(boolean loadLatestVersion) {
    this.loadLatestVersion = loadLatestVersion;
  }

}
