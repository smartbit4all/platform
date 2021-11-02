package org.smartbit4all.domain.data.storage;

import org.smartbit4all.api.storage.bean.ObjectVersion;

public class StorageObjectHistoryEntry<T> {

  private ObjectVersion version;

  private T object;

  public StorageObjectHistoryEntry(ObjectVersion version, T object) {
    super();
    this.version = version;
    this.object = object;
  }

  public final ObjectVersion getVersion() {
    return version;
  }

  public final void setVersion(ObjectVersion version) {
    this.version = version;
  }

  public final T getObject() {
    return object;
  }

  public final void setObject(T object) {
    this.object = object;
  }

}
