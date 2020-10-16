package org.smartbit4all.core.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * For very short mapping (typically one-to-one) it's faster and consumes less memory.
 * 
 * @author Peter Boros
 *
 * @param <K>
 * @param <V>
 */
public class ListBasedMap<K, V> implements Map<K, V> {


  public class KeyValuePair implements Entry<K, V> {

    K key;

    V value;

    public KeyValuePair(K key, V value) {
      super();
      this.key = key;
      this.value = value;
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    public V setValue(V value) {
      V prev = this.value;
      this.value = value;
      return prev;
    }

  }

  /**
   * These are the pairs that stored in the map.
   */
  List<Entry<K, V>> entries = new ArrayList<>(1);

  @Override
  public int size() {
    return entries.size();
  }

  @Override
  public boolean isEmpty() {
    return entries.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    for (Entry<K, V> entry : entries) {
      if (entry.getKey() == key) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsValue(Object value) {
    for (Entry<K, V> entry : entries) {
      if (entry.getValue() == value) {
        return true;
      }
    }
    return false;
  }

  @Override
  public V get(Object key) {
    for (Entry<K, V> entry : entries) {
      if (entry.getKey() == key) {
        return entry.getValue();
      }
    }
    return null;
  }

  @Override
  public V put(K key, V value) {
    for (Entry<K, V> entry : entries) {
      if (entry.getKey() == key) {
        V previousValue = entry.getValue();
        entry.setValue(value);
        return previousValue;
      }
    }
    entries.add(new KeyValuePair(key, value));
    return null;
  }

  @Override
  public V remove(Object key) {
    int idxToRemove = 0;
    V previousValue = null;
    boolean remove = false;
    for (Entry<K, V> entry : entries) {
      if (entry.getKey() == key) {
        previousValue = entry.getValue();
        remove = true;
        break;
      }
      idxToRemove++;
    }
    if (remove) {
      entries.remove(idxToRemove);
    }
    return previousValue;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void clear() {
    entries.clear();
  }

  @Override
  public Set<K> keySet() {
    Set<K> result = new HashSet<>();
    for (Entry<? extends K, ? extends V> entry : entries) {
      result.add(entry.getKey());
    }
    return result;
  }

  @Override
  public Collection<V> values() {
    Collection<V> result = new ArrayList<>();
    for (Entry<? extends K, ? extends V> entry : entries) {
      result.add(entry.getValue());
    }
    return result;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return new HashSet<Map.Entry<K, V>>(entries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Entry<K, V> entry : entries) {
      if (sb.length() > 0) {
        sb.append(StringConstant.COMMA_SPACE);
      }
      sb.append(entry.getKey());
      sb.append(StringConstant.ARROW);
      sb.append(entry.getValue());
    }
    return sb.toString();
  }

}
