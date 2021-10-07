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

  List<StorageIndex<T>> indexList = new ArrayList<>();

  protected final List<StorageIndex<T>> get() {
    return indexList;
  }

}
