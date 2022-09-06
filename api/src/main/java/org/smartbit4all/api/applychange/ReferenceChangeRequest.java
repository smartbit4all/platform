package org.smartbit4all.api.applychange;

import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * The reference change request is the abstract super class of the different reference change
 * requests
 * 
 * @author Peter Boros
 */
public abstract class ReferenceChangeRequest {

  private ReferenceDefinition definition;

  public ReferenceChangeRequest(ReferenceDefinition definition) {
    super();
    this.definition = definition;
  }

}
