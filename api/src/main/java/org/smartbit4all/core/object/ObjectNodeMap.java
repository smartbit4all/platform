package org.smartbit4all.core.object;

import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;

public final class ObjectNodeMap {

  private final Map<String, ObjectNodeReference> map;

  private final ObjectNode referrerNode;
  private final ReferenceDefinition referenceDefinition;

  public ObjectNodeMap(ObjectApi objectApi, ObjectNode referrerNode,
      ReferenceDefinition referenceDefinition,
      Map<String, URI> originalUris, Map<String, ObjectNodeData> originalMap) {
    super();
    Objects.requireNonNull(objectApi, "ObjectApi must not be null!");
    Objects.requireNonNull(referrerNode, "ReferrerNode must not be null!");
    Objects.requireNonNull(originalUris, "OriginalUris must not be null!");

    this.referrerNode = referrerNode;
    this.referenceDefinition = referenceDefinition;
    if (originalMap != null) {
      // loaded
      if (originalMap.size() != originalUris.size()) {
        throw new IllegalArgumentException("originalMap and originalUris size doesn't match!");
      }
      map = originalMap.entrySet().stream()
          .collect(toMap(
              Entry::getKey,
              e -> new ObjectNodeReference(
                  referrerNode, referenceDefinition,
                  originalUris.get(e.getKey()),
                  new ObjectNode(objectApi, e.getValue()))));
    } else {
      // not loaded
      map = originalUris.entrySet().stream()
          .collect(toMap(
              Entry::getKey,
              e -> new ObjectNodeReference(
                  referrerNode, referenceDefinition,
                  originalUris.get(e.getKey()),
                  null)));

    }
  }

  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  public ObjectNodeReference get(Object key) {
    return map.get(key);
  }

  public ObjectNode putNewObject(String key, Object object) {
    ObjectNode newNode = referrerNode.objectApi.create(referrerNode.getStorageScheme(), object);
    newNode.setState(ObjectNodeState.NEW);
    put(key, newNode);
    return newNode;
  }

  public ObjectNodeReference put(String key, ObjectNode node) {
    ObjectNodeReference ref = map.computeIfAbsent(key,
        k -> new ObjectNodeReference(referrerNode, referenceDefinition, null, null));
    ref.set(node);
    return ref;
  }

  public ObjectNodeReference put(String key, URI uri) {
    ObjectNodeReference ref = map.computeIfAbsent(key,
        k -> new ObjectNodeReference(referrerNode, referenceDefinition, null, null));
    ref.set(uri);
    return ref;
  }

  public Set<String> keySet() {
    return map.keySet();
  }

  public Collection<ObjectNodeReference> values() {
    return map.values();
  }

  public Set<Entry<String, ObjectNodeReference>> entrySet() {
    return map.entrySet();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectNodeMap list = (ObjectNodeMap) o;
    return Objects.equals(this.map, list.map);
  }

  @Override
  public int hashCode() {
    return Objects.hash(map);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    // @formatter:off
    sb.append("class ObjectNodeMap{\n");
    sb.append("    map: ").append(toIndentedString(map)).append("\n");
    sb.append("}");
    // @formatter:on
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
