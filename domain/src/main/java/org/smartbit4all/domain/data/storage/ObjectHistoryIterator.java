package org.smartbit4all.domain.data.storage;

public abstract class ObjectHistoryIterator implements Iterable<StorageObjectHistoryEntry<?>> {

  protected long i = -1;

  public long getI() {
    return i;
  }

}
