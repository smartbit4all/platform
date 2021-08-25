package org.smartbit4all.api.binarydata;

import java.io.InputStream;
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
    if (!binaryContent.isLoaded()) {
      load(binaryContent);
    }

    return binaryContent.getData().inputStream();
  }

  @Override
  public BinaryContent contentOf(BinaryContent binaryContent, InputStream inputstream) {
    setBinaryData(binaryContent, BinaryData.of(inputstream));
    binaryContent.setSaveData(true);
    return binaryContent;
  }

}
