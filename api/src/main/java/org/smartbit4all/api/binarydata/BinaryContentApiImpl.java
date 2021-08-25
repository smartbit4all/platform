package org.smartbit4all.api.binarydata;

import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

public class BinaryContentApiImpl implements BinaryContentApi {

  protected BinaryDataApi binaryDataApi;

  public BinaryContentApiImpl(BinaryDataApi binaryDataApi) {
    super();
    this.binaryDataApi = binaryDataApi;
  }

  @Override
  public void load(BinaryContent... binaryContents) {
    for (BinaryContent binaryContent : binaryContents) {
      Optional<BinaryData> binaryData = binaryDataApi.load(binaryContent.getDataUri());

      if (!binaryData.isPresent()) {
        throw new IllegalStateException(
            "No BinaryData found in '" + binaryContent.getDataUri() + "'");
      }
      setBinaryData(binaryContent, binaryData.get());
    }
  }

  protected void setBinaryData(BinaryContent binaryContent, BinaryData binaryData) {
    binaryContent.setData(binaryData);
    binaryContent.setLoaded(true);
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

    return binaryContent.getData();
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

    binaryContent.setSaveData(true);
    binaryContent.setDataUri(dataUri);

    setBinaryData(binaryContent, binaryData);
  }

  @Override
  public void uploadContent(BinaryContent binaryContent, InputStream inputstream,
      URI dataUri) {
    BinaryData data = BinaryData.of(inputstream);
    uploadContent(binaryContent, data, dataUri);
  }

  @Override
  public void uploadContent(BinaryContent binaryContent, BinaryData data, URI dataUri) {
    binaryContent.setDataUri(dataUri);

    setBinaryData(binaryContent, data);

    binaryDataApi.save(data, dataUri);
  }
}
