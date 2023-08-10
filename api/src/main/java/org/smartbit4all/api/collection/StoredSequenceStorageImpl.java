package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.List;

public class StoredSequenceStorageImpl implements StoredSequence {

  private StorageSequenceApi sequenceApi;

  private URI uri;

  StoredSequenceStorageImpl(URI uri, String name,
      StorageSequenceApi sequenceApi) {
    this.sequenceApi = sequenceApi;
    this.uri = uri;
  }

  @Override
  public Long next() {
    return sequenceApi.next(uri);
  }

  @Override
  public List<Long> next(int count) {
    return sequenceApi.next(uri, count);
  }

}
