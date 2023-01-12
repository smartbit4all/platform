package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.List;
import org.smartbit4all.domain.data.storage.Storage;

public class StoredSequenceStorageImpl extends AbstractStoredContainerStorageImpl
    implements StoredSequence {

  private StorageSequenceApi sequenceApi;

  StoredSequenceStorageImpl(Storage storage, URI uri, String name,
      StorageSequenceApi sequenceApi) {
    super(storage, uri, name);
    this.sequenceApi = sequenceApi;
  }

  @Override
  public Long next() {
    return sequenceApi.next(getUri());
  }

  @Override
  public List<Long> next(int count) {
    return sequenceApi.next(getUri(), count);
  }

}
