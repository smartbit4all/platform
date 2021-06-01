package org.smartbit4all.domain.data.index;

import java.util.List;

/**
 * @author Peter Boros
 *
 * @param <K>
 * @param <V>
 */
public abstract class StorageNonUniqueIndex<K, V> implements StorageIndex {

  /**
   * Retrieve the values (typically the primary keys) matching the given value.
   * 
   * @param value
   * @return
   */
  public abstract List<V> get(K value);

}
