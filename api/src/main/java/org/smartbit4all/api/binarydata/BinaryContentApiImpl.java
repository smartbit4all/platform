package org.smartbit4all.api.binarydata;

import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;

public class BinaryContentApiImpl implements BinaryContentApi {

  protected BinaryDataApi binaryDataApi;

  @Autowired
  private StorageApi storageApi;

  public BinaryContentApiImpl(BinaryDataApi binaryDataApi) {
    super();
    this.binaryDataApi = binaryDataApi;
  }

  @Override
  public void load(BinaryContent... binaryContents) {
    for (BinaryContent binaryContent : binaryContents) {
      URI dataUri = binaryContent.getDataUri();
      Optional<BinaryData> binaryData = binaryDataApi.load(binaryContent.getDataUri());

      if (!binaryData.isPresent()) {
        Storage storage = dataUri == null ? null : storageApi.get(dataUri.getScheme());
        if (storage != null) {
          BinaryDataObject binaryDataObject = storage.read(dataUri, BinaryDataObject.class).get();
          binaryData = Optional.of(binaryDataObject.getBinaryData());
        }
      }

      if (!binaryData.isPresent()) {
        throw new IllegalStateException(
            "No BinaryData found in '" + binaryContent.getDataUri() + "'!");
      }
      setBinaryData(binaryContent, binaryData.get(), false);
    }
  }

  @Override
  public InputStream getInputStream(BinaryContent binaryContent) {
    return getBinaryData(binaryContent).inputStream();
  }

  @Override
  public BinaryData getBinaryData(BinaryContent binaryContent) {
    if (!binaryContent.isLoaded()) {
      load(binaryContent);
    }

    BinaryContent unwrappedBinaryContent = ApiObjectRef.unwrapObject(binaryContent);
    return unwrappedBinaryContent.getData();
  }

  @Override
  public void saveIntoContent(BinaryContent binaryContent, InputStream inputstream,
      URI dataUri) {

    BinaryData binaryData = BinaryData.of(inputstream);
    saveIntoContent(binaryContent, binaryData, dataUri);
  }

  @Override
  public void saveIntoContent(BinaryContent binaryContent, BinaryData binaryData,
      URI dataUri) {

    binaryContent.setDataUri(dataUri);
    setBinaryData(binaryContent, binaryData, true);
  }

  @Override
  public void uploadContent(BinaryContent binaryContent, InputStream inputstream,
      URI dataUri) {
    BinaryData data = BinaryData.of(inputstream);
    uploadContent(binaryContent, data, dataUri);
  }

  @Override
  public void uploadContent(BinaryContent binaryContent, BinaryData binaryData, URI dataUri) {
    binaryContent.setDataUri(dataUri);

    setBinaryData(binaryContent, binaryData, false);

    binaryDataApi.save(binaryData, dataUri);
  }


  protected void setBinaryData(BinaryContent binaryContent, BinaryData binaryData,
      boolean saveData) {
    BinaryContent unwrappedBinaryContent = ApiObjectRef.unwrapObject(binaryContent);
    binaryContent.setSize(binaryData.length());

    unwrappedBinaryContent.setData(binaryData);
    unwrappedBinaryContent.setLoaded(true);
    unwrappedBinaryContent.setSaveData(saveData);
  }

  @Override
  public void removeContent(BinaryContent binaryContent) {
    binaryDataApi.remove(binaryContent.getDataUri());

    BinaryContent unwrappedBinaryContent = ApiObjectRef.unwrapObject(binaryContent);
    binaryContent.setDataUri(null);
    binaryContent.setSize(null);

    unwrappedBinaryContent.setLoaded(false);
    unwrappedBinaryContent.setSaveData(false);
    unwrappedBinaryContent.setData(null);
  }
}
