package org.smartbit4all.core.object;

import org.smartbit4all.api.object.bean.ObjectNodeData;

public class ObjectNodeReference {

  /**
   * The original data.
   */
  private final ObjectNodeData original;

  private final ObjectNode ref;

  public ObjectNodeReference(ObjectNodeData data) {
    this.original = data;
    this.ref = null;
  }

}
