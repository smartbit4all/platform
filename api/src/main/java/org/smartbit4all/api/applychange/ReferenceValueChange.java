package org.smartbit4all.api.applychange;

import org.smartbit4all.core.object.ReferenceDefinition;

public class ReferenceValueChange extends ReferenceChangeRequest {

  public ReferenceValueChange(ReferenceDefinition definition) {
    super(definition);
  }

  public enum ReferenceValueOperation {
    SET, REMOVE, UPDATE
  }

}
