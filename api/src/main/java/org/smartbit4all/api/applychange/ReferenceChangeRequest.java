package org.smartbit4all.api.applychange;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Map;
import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * The reference change request is the abstract super class of the different reference change
 * requests
 * 
 * @author Peter Boros
 */
public abstract class ReferenceChangeRequest {

  /**
   * To access the request and better destruction at the same time.
   */
  private WeakReference<ApplyChangeRequest> requestRef;

  /**
   * To access the object change and better destruction at the same time.
   */
  protected WeakReference<ObjectChangeRequest> objectChangeRef;

  /**
   * The reference definition.
   */
  protected ReferenceDefinition definition;

  public enum ReferenceOperation {
    SET, REMOVE, UPDATE
  }

  /**
   * The operation for the reference.
   */
  protected ReferenceOperation operation;

  protected ReferenceChangeRequest(ApplyChangeRequest request,
      ObjectChangeRequest objectChangeRequest, ReferenceDefinition definition) {
    super();
    this.definition = definition;
    this.requestRef = new WeakReference<>(request);
    this.objectChangeRef = new WeakReference<>(objectChangeRequest);
  }

  public final ApplyChangeRequest request() {
    return requestRef != null ? requestRef.get() : null;
  }

  public final ObjectChangeRequest object() {
    return objectChangeRef != null ? objectChangeRef.get() : null;
  }

  public abstract Iterable<ObjectChangeRequest> changes();

  public abstract void apply(ObjectChangeRequest object, Map<ObjectChangeRequest, URI> uris);

  public final ReferenceOperation getOperation() {
    return operation;
  }

}
