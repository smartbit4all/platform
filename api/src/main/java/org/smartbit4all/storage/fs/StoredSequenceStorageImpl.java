package org.smartbit4all.storage.fs;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.collection.StoredSequence;
import org.smartbit4all.api.collection.bean.StoredSequenceData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class StoredSequenceStorageImpl implements StoredSequence {

  private URI uri;

  StorageApi storageApi;

  private PlatformTransactionManager transactionManager;

  private static final Long START_VALUE = Long.valueOf(0);

  public StoredSequenceStorageImpl(PlatformTransactionManager transactionManager,
      StorageApi storageApi, URI uri, String name) {
    this.transactionManager = transactionManager;
    this.uri = uri;
    this.storageApi = storageApi;
  }

  @Override
  public Long next() {
    return next(1).get(0);
  }

  @Override
  public List<Long> next(int count) {
    Objects.requireNonNull(uri, "The uri of the sequence is missing.");
    if (count <= 0) {
      throw new IllegalArgumentException("The next must be called with positive number");
    }
    if (transactionManager != null) {
      TransactionTemplate transaction = new TransactionTemplate(transactionManager);
      transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
      return transaction.execute(status -> {
        return nextInternal(count);
      });
    } else {
      return nextInternal(count);
    }
  }

  private List<Long> nextInternal(int count) {
    Storage storage = storageApi.getStorage(uri);
    Objects.requireNonNull(storage,
        "Unable to identify the storage for the " + uri + " sequence.");


    List<Long> results = new ArrayList<>();
    StorageObjectLock objectLock = storage.getLock(uri);
    objectLock.lock();
    try {
      if (storage.exists(uri)) {
        storage.update(uri, StoredSequenceData.class, s -> {
          for (int i = 0; i < count; i++) {
            results.add(s.current(s.getCurrent() + 1).getCurrent());
          }
          return s;
        });
      } else {
        StoredSequenceData s = new StoredSequenceData().current(START_VALUE).uri(uri);
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

  @Override
  public Long current() {
    if (uri == null) {
      return START_VALUE;
    }
    Storage storage = storageApi.getStorage(uri);
    if (storage == null) {
      return START_VALUE;
    }
    if (storage.exists(uri)) {
      StoredSequenceData sequenceData = storage.read(uri, StoredSequenceData.class);
      return sequenceData.getCurrent();
    }
    return START_VALUE;
  }

  @Override
  public Long set(Long newValue) {
    Storage storage = storageApi.getStorage(uri);
    Objects.requireNonNull(storage,
        "Unable to identify the storage for the " + uri + " sequence.");


    StorageObjectLock objectLock = storage.getLock(uri);
    objectLock.lock();
    try {
      if (storage.exists(uri)) {
        storage.update(uri, StoredSequenceData.class, s -> {
          return s.current(newValue);
        });
      } else {
        StoredSequenceData s = new StoredSequenceData().current(START_VALUE).uri(uri);
        s.current(newValue);
        storage.saveAsNew(s);
      }
    } finally {
      objectLock.unlock();
    }
    return newValue;
  }

}
