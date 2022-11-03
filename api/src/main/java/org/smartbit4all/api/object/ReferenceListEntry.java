package org.smartbit4all.api.object;

import java.util.List;

class ReferenceListEntry {
  ObjectNodeList publicNodeList;
  List<ObjectNode> nodeList;

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

  final List<ObjectNode> getNodeList() {
    return nodeList;
  }

}
