package org.smartbit4all.domain.data.storage;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.storage.index.StorageIndex;

/**
 * This is a typed index list for an object.
 * 
 * @author Peter Boros
 */
public class StorageObjectIndices<T> {

  private final String scheme;

  private final Class<T> clazz;

  final List<StorageIndex<T>> indexList = new ArrayList<>();

  public StorageObjectIndices(String scheme, Class<T> clazz) {
    super();
    this.scheme = scheme;
    this.clazz = clazz;
  }

  public final List<StorageIndex<T>> get() {
    return indexList;
  }

  public String getScheme() {
    return scheme;
  }

  public Class<T> getClazz() {
    return clazz;
  }

}
