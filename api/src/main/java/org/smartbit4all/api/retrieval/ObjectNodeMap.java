package org.smartbit4all.api.retrieval;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.smartbit4all.api.retrieval.ObjectNode.ObjectNodeState;

public class ObjectNodeMap implements Map<String, ObjectNode> {

  private final Map<String, ObjectNode> originalMap;

  private final Map<String, ObjectNode> map;

  public ObjectNodeMap(Map<String, ObjectNode> originalMap) {
    super();
    this.originalMap = originalMap;
    map = originalMap.entrySet().stream().filter(e -> e.getValue().isRemoved())
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
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
        ObjectNode originalNode = originalMap.get(key);
        if (originalNode == objectNode) {
          // The new node now execute a removal on the original one.
          originalNode.setState(ObjectNodeState.REMOVED);
        }
      }
    } else {
      ObjectNode originalNode = originalMap.get(key);
      if (originalNode != null) {
        if (originalNode.getState() == ObjectNodeState.REMOVED && originalNode == value) {
          // This restores the given node.
          originalNode.setModified();
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
