package org.smartbit4all.domain.data.index;

import java.util.List;
import org.smartbit4all.domain.meta.Property;

/**
 * @author Peter Boros
 *
 * @param <K>
 * @param <V>
 */
public abstract class StorageNonUniqueIndex<K, V> implements StorageIndex {

  private Property<?> indexProperty;

  /**
   * Retrieve the values (typically the primary keys) matching the given key.
   * 
   * @param key
   * @return
   */
  public abstract List<V> get(K key);

}
