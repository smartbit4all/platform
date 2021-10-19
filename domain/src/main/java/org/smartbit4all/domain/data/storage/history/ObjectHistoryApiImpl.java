package org.smartbit4all.domain.data.storage.history;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.storage.bean.ObjectHistory;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;

public class ObjectHistoryApiImpl implements ObjectHistoryApi {

  private StorageApi storageApi;

  public ObjectHistoryApiImpl(StorageApi storageApi) {
    this.storageApi = storageApi;
  }

  @Override
  public ObjectHistory getObjectHistory(URI objectUri, String scheme) {
    Storage storage = storageApi.get(scheme);
    List<ObjectHistoryEntry> objectHistoryEntries = storage.loadHistory(objectUri);

    return new ObjectHistory()
        .objectHistoryEntries(objectHistoryEntries);
  }
}
