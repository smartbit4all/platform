package org.smartbit4all.api.object;

import java.util.HashMap;
import java.util.Map;
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
  private boolean loadLatestVersion = false;

  /**
   * The objects to load trough the references. The {@link ReferenceDefinition} is the key.
   */
  private final Map<ReferenceDefinition, ObjectRetrievalRequest> references = new HashMap<>();

  /**
   * The object request is constructed by the {@link RetrievalApi}.
   * 
   */
  ObjectRetrievalRequest(boolean loadLatestVersion) {
    super();
    this.loadLatestVersion = loadLatestVersion;
  }

  /**
   * The object request is constructed by itself and the {@link RetrievalRequest}.
   * 
   */
  ObjectRetrievalRequest() {
    this(false);
  }

  /**
   * @return The Map of the referred {@link ObjectRetrievalRequest}. They are mapped by the
   *         {@link ReferenceDefinition} we can access them through.
   */
  public final Map<ReferenceDefinition, ObjectRetrievalRequest> getReferences() {
    return references;
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
        r -> new ObjectRetrievalRequest(loadLatestVersion));
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
