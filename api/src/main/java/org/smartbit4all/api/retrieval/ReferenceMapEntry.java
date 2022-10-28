package org.smartbit4all.api.retrieval;

import java.util.Map;

class ReferenceMapEntry {

  public ObjectNodeMap publicNodeMap;
  public Map<String, ObjectNode> nodeMap;

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


}
