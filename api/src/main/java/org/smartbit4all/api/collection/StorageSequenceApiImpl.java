package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.collection.bean.StoredSequenceData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.springframework.beans.factory.annotation.Autowired;

public class StorageSequenceApiImpl implements StorageSequenceApi {

  @Autowired
  private StorageApi storageApi;

  @Override
  public Long next(URI sequenceURI) {
    return next(sequenceURI, 1).get(0);
  }

  @Override
  public List<Long> next(URI sequenceURI, int count) {
    Objects.requireNonNull(sequenceURI, "The uri of the sequence is missing.");
    if (count <= 0) {
      throw new IllegalArgumentException("The next must be called with positive number");
    }
    Storage storage = storageApi.getStorage(sequenceURI);
    Objects.requireNonNull(storage,
        "Unable to identify the storage for the " + sequenceURI + " sequence.");


    List<Long> results = new ArrayList<>();
    StorageObjectLock objectLock = storage.getLock(sequenceURI);
    objectLock.lock();
    try {
      if (storage.exists(sequenceURI)) {
        storage.update(sequenceURI, StoredSequenceData.class, s -> {
          for (int i = 0; i < count; i++) {
            results.add(s.current(s.getCurrent() + 1).getCurrent());
          }
          return s;
        });
      } else {
        StoredSequenceData s = new StoredSequenceData().current(Long.valueOf(0)).uri(sequenceURI);
        for (int i = 0; i < count; i++) {
          results.add(s.current(s.getCurrent() + 1).getCurrent());
        }
        storage.saveAsNew(s);
      }
    } finally {
      objectLock.unlock();
    }
    return results;
  }

}
