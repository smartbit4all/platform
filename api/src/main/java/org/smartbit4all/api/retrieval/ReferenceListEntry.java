package org.smartbit4all.api.retrieval;

import java.util.List;

public class ReferenceListEntry {
  public ObjectNodeList publicNodeList;
  public List<ObjectNode> nodeList;

  public ReferenceListEntry(List<ObjectNode> nodeList) {
    super();
    this.nodeList = nodeList;
  }

  public ObjectNodeList getPublicNodeList() {
    if (publicNodeList == null) {
      publicNodeList = new ObjectNodeList(nodeList);
    }
    return publicNodeList;
  }
}
