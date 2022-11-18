package org.smartbit4all.api.object;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.core.object.ObjectApi;

class ObjectNodeMap implements Map<String, ObjectNode> {

  private final Map<String, ObjectNodeData> originalMap;

  private final Map<String, ObjectNode> map;

  public ObjectNodeMap(ObjectApi objectApi, Map<String, ObjectNodeData> originalMap) {
    super();
    this.originalMap = originalMap;
    map = originalMap.entrySet().stream()
        .filter(e -> e.getValue().getState() != ObjectNodeState.REMOVED)
        .collect(Collectors.toMap(
            Entry<String, ObjectNodeData>::getKey,
            e -> ObjectNodes.of(objectApi, e.getValue())));
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public ObjectNode get(Object key) {
    return map.get(key);
  }

  @Override
  public ObjectNode put(String key, ObjectNode value) {
    // The options are:
    // - Brand new object --> clear we add this.
    // - Existing node in the map --> It is an update with the content.
    // - Existing node in the original map --> appears in the map and it's an update
    ObjectNode objectNode = map.get(key);
    if (objectNode != null) {
      // We have the given node in the map.
      if (objectNode != value && objectNode.getState() != ObjectNodeState.NEW) {
        // It must be a modification depending on the equivalence of the object node. If they are
        // not the same then we remove the current one and put the new one.
        ObjectNodeData originalNode = originalMap.get(key);
        if (originalNode == objectNode.getData()) { // TODO ref. check or value check?
          // The new node now execute a removal on the original one.
          originalNode.setState(ObjectNodeState.REMOVED);
        }
      }
    } else {
      ObjectNodeData originalNode = originalMap.get(key);
      if (originalNode != null) {
        if (originalNode.getState() == ObjectNodeState.REMOVED && originalNode == value.getData()) {
          // This restores the given node.
          originalNode.setState(ObjectNodeState.MODIFIED);
        }
      }
    }
    return map.put(key, value);
  }

  @Override
  public ObjectNode remove(Object key) {
    ObjectNode objectNode = map.remove(key);
    if (objectNode != null && objectNode.getState() != ObjectNodeState.NEW) {
      objectNode.setState(ObjectNodeState.REMOVED);
    }
    return objectNode;
  }

  @Override
  public void putAll(Map<? extends String, ? extends ObjectNode> m) {
    if (m != null) {
      m.entrySet().stream().forEach(e -> put(e.getKey(), e.getValue()));
    }
  }

  @Override
  public void clear() {
    List<String> collect = map.keySet().stream().collect(Collectors.toList());
    collect.stream().forEach(k -> remove(k));
  }

  @Override
  public Set<String> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<ObjectNode> values() {
    return map.values();
  }

  @Override
  public Set<Entry<String, ObjectNode>> entrySet() {
    return map.entrySet();
  }

}
