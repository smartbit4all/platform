package org.smartbit4all.api.mimetype;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;

public class ContentConversionContributionApiOctetStreamToText
    extends ContentConversionContributionApiImpl implements ContentConversionContributionApi {

  public static final String API_NAME = "ContentConversionContributionApiOctetStreamToText";

  public ContentConversionContributionApiOctetStreamToText() {
    super(API_NAME);
  }

  @Override
  public List<String> getAcceptedMimeTypes() {
    return List.of(MimeTypeApi.BINARY_MIMETYPE);
  }

  @Override
  public List<String> getTargetMimeTypes() {
    return List.of(MimeTypeApi.TXT_MIMETYPE);
  }

  @Override
  protected BinaryData convertInternal(BinaryContentData content, String toMimeType,
      Map<String, Object> parameters) {
    URI dataUri = content.getDataUri();
    if (dataUri == null) {
      return null;
    }
    BinaryDataObject object = objectApi.loadLatest(dataUri).getObject(BinaryDataObject.class);
    BinaryData binaryData = object.getBinaryData();
    try (InputStream is = binaryData.inputStream()) {
      byte[] bytes = is.readAllBytes();
      String byteString = new String(bytes);
      return new BinaryData(byteString.getBytes());
    } catch (IOException e) {
      throw getFailedException(content, toMimeType, e);
    }
  }

}
