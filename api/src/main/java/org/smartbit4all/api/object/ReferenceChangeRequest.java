package org.smartbit4all.api.object;

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
   * The reference definition.
   */
  protected ReferenceDefinition definition;

  protected ReferenceChangeRequest(ReferenceDefinition definition) {
    super();
    this.definition = definition;
  }

  public abstract Iterable<ObjectChangeRequest> changes();

  public abstract void apply(ObjectChangeRequest object, Map<ObjectChangeRequest, Object> uris);

}
