package org.smartbit4all.core.object;

import java.util.Objects;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;

public class ObjectNodeReference {

  /**
   * The original data.
   */
  private ObjectNodeData data;

  private ObjectNode node;

  ObjectNodeReference(ObjectNode node) {
    this.data = node.getData();
    this.node = node;
  }

  public void set(ObjectNode node) {
    Objects.requireNonNull(node, "ObjectNode must not be null! For clearing ref, use clear()");
    this.node = node;
    this.data = node.getData();
    if (this.data.getState() == ObjectNodeState.NOP) {
      this.data.setState(ObjectNodeState.NEW);
    }
  }

  public ObjectNode get() {
    return node;
  }

  public void clear() {
    if (data != null) {
      data.setState(ObjectNodeState.REMOVED);
    }
    // node = null;
  }

  public boolean isPresent() {
    return node != null;
  }

}
