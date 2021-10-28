package org.smartbit4all.core.object;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class ApiObjectMapProxy<T> implements Map<String, T> {

  /**
   * The original map.
   */
  private final ApiObjectMap map;

  ApiObjectMapProxy(ApiObjectMap map) {
    super();
    this.map = map;
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
    return map.containsValueObject(value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T get(Object key) {
    ApiObjectRef objectRef = map.get(key);
    return objectRef != null ? (T) objectRef.getWrapper() : null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T put(String key, T value) {
    ApiObjectRef apiObjectRef = map.putObject(key, value);
    if (apiObjectRef != null) {
      return (T) apiObjectRef.getWrapper();
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T remove(Object key) {
    ApiObjectRef objectRef = map.remove(key);
    return objectRef != null ? (T) objectRef.getObject() : null;
  }

  @Override
  public void putAll(Map<? extends String, ? extends T> m) {
    if (m != null) {
      for (Entry<? extends String, ? extends T> entry : m.entrySet()) {
        map.putObject(entry.getKey(), entry.getValue());
      }
    }
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Set<String> keySet() {
    return map.keySet();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<T> values() {
    return map.values().stream().map(v -> (T) v.getWrapper()).collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  @Override
  public Set<Entry<String, T>> entrySet() {
    return map.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> (T) e.getValue().getWrapper())).entrySet();
  }

}
