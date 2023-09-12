package org.smartbit4all.core.object;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * A {@link Map} implementation where {@code null} values are first class citizens.
 * 
 * @author Szabolcs Bazil Papp
 *
 */
public class ObjectMap implements Map<String, Object> {

  private final Set<String> keySet;
  private final Map<String, Object> innerMap;

  public ObjectMap() {
    this.keySet = new TreeSet<>();
    this.innerMap = new LinkedHashMap<>();
  }

  ObjectMap(ObjectDefinition<?> definition) {
    this();
    definition.getPropertiesByName().keySet().forEach(s -> put(s, null));
    definition.getOutgoingReferences().keySet().forEach(s -> put(s, null));
  }

  @Override
  public int size() {
    return keySet.size();
  }

  @Override
  public boolean isEmpty() {
    return keySet.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return keySet.contains(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return innerMap.containsValue(value);
  }

  @Override
  public Object get(Object key) {
    return innerMap.get(key);
  }

  @Override
  public Object put(String key, Object value) {
    keySet.add(key);
    return innerMap.put(key, value);
  }

  @Override
  public Object remove(Object key) {
    keySet.remove(key);
    return innerMap.remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m) {
    keySet.addAll(m.keySet());
    innerMap.putAll(m);
  }

  @Override
  public void clear() {
    keySet.clear();
    innerMap.clear();
  }

  @Override
  public Set<String> keySet() {
    return new TreeSet<>(keySet);
  }

  @Override
  public Collection<Object> values() {
    return innerMap.values();
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    return keySet.stream().map(ObjectMapEntry::new).collect(Collectors.toSet());
  }

  final class ObjectMapEntry implements Map.Entry<String, Object> {

    private final String key;

    private ObjectMapEntry(String key) {
      this.key = key;
    }

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public Object getValue() {
      return ObjectMap.this.innerMap.get(key);
    }

    @Override
    public Object setValue(Object value) {
      return ObjectMap.this.innerMap.put(key, value);
    }

  }

}
