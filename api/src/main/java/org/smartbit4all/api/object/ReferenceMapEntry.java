package org.smartbit4all.api.object;

import java.util.Map;

class ReferenceMapEntry {

  ObjectNodeMap publicNodeMap;
  Map<String, ObjectNode> nodeMap;

  public ReferenceMapEntry(Map<String, ObjectNode> nodeMap) {
    super();
    this.nodeMap = nodeMap;
  }

  public ObjectNodeMap getPublicNodeMap() {
    if (publicNodeMap == null) {
      publicNodeMap = new ObjectNodeMap(nodeMap);
    }
    return publicNodeMap;
  }

  final Map<String, ObjectNode> getNodeMap() {
    return nodeMap;
  }

}
