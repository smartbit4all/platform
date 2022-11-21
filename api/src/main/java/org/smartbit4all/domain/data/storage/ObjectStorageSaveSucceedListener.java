package org.smartbit4all.domain.data.storage;

/**
 * Implementations of this interface can be registered in Spring configurations and are calls on
 * every storage save.
 */
public interface ObjectStorageSaveSucceedListener {

  void doOnSave(StorageSaveEvent event);

  boolean supportsType(Class<?> type);

  boolean supportsSchema(String schema);

}
