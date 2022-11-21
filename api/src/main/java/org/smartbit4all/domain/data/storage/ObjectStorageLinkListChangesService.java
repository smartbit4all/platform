package org.smartbit4all.domain.data.storage;

import org.smartbit4all.api.storage.bean.StorageSaveEventObject;

public interface ObjectStorageLinkListChangesService {

  void addNewChangeListener(ObjectStorageLinkedListChangeListener listener);

  void addNewChangeListener(ObjectStorageLinkedListChangeListener listener,
      StorageSaveEventObject applyFrom);

  StorageSaveEventObject getFirst();

  StorageSaveEventObject getLast();

  StorageSaveEventObject getNext(StorageSaveEventObject currentEventObject);

  void doChangesOnListFrom(StorageSaveEventObject fromEvent,
      ObjectStorageLinkedListChangeListener listener);

  public static interface ObjectStorageLinkedListChangeListener {

    void onChange(StorageSaveEventObject saveEventObject,
        ObjectStorageLinkListChangesService changeService);

  }

}
