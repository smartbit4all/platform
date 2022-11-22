package org.smartbit4all.core.object;

import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.smartbit4all.api.object.bean.ObjectNodeData;

public final class ObjectNodeMap {

  private final Map<String, ObjectNodeReference> map;

  private final ObjectNode referrerNode;

  public ObjectNodeMap(ObjectApi objectApi, ObjectNode referrerNode, Map<String, URI> originalUris,
      Map<String, ObjectNodeData> originalMap) {
    super();
    Objects.requireNonNull(objectApi, "ObjectApi must not be null!");
    Objects.requireNonNull(referrerNode, "ReferrerNode must not be null!");
    Objects.requireNonNull(originalUris, "OriginalUris must not be null!");

    this.referrerNode = referrerNode;
    if (originalMap != null) {
      // loaded
      if (originalMap.size() != originalUris.size()) {
        throw new IllegalArgumentException("originalMap and originalUris size doesn't match!");
      }
      map = originalMap.entrySet().stream()
          .collect(toMap(
              Entry::getKey,
              e -> new ObjectNodeReference(
                  referrerNode,
                  originalUris.get(e.getKey()),
                  objectApi.node(e.getValue()))));
    } else {
      // not loaded
      map = originalUris.entrySet().stream()
          .collect(toMap(
              Entry::getKey,
              e -> new ObjectNodeReference(
                  referrerNode,
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

  public ObjectNodeReference put(String key, ObjectNode node) {
    ObjectNodeReference ref = map.computeIfAbsent(key,
        k -> new ObjectNodeReference(referrerNode, null, null));
    ref.set(node);
    return ref;
  }

  public ObjectNodeReference put(String key, URI uri) {
    ObjectNodeReference ref = map.computeIfAbsent(key,
        k -> new ObjectNodeReference(referrerNode, null, null));
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

}
