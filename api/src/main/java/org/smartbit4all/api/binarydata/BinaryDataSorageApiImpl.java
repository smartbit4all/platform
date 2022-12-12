package org.smartbit4all.api.binarydata;

import java.net.URI;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;

public class BinaryDataSorageApiImpl implements BinaryDataSorageApi {

  @Autowired
  private StorageApi storageApi;

  @Override
  public URI save(String logicalSchema, BinaryData data) {
    Storage storage = storageApi.get(logicalSchema);
    return storage.saveAsNew(data.asObject());
  }

  @Override
  public URI update(URI uri, BinaryData data) {
    Storage storage = storageApi.getStorage(uri);
    if (storage != null) {
      return storage.update(uri, BinaryDataObject.class, d -> data.asObject());
    }
    return uri;
  }

  @Override
  public BinaryData load(URI uri) {
    return ((BinaryDataObject) storageApi.load(uri).getObject()).getBinaryData();
  }

  @Override
  public BinaryData loadLatest(URI uri) {
    return ((BinaryDataObject) storageApi.load(ObjectStorageImpl.getUriWithoutVersion(uri))
        .getObject()).getBinaryData();
  }

}
