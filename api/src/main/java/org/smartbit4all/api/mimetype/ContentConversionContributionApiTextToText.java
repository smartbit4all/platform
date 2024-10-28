package org.smartbit4all.api.mimetype;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;

public class ContentConversionContributionApiTextToText extends ContentConversionContributionApiImpl
    implements ContentConversionContributionApi {

  public static final String API_NAME = "TEXT_TO_TEXT_CONVERTER";

  public ContentConversionContributionApiTextToText() {
    super(API_NAME);
  }

  @Override
  public List<String> getAcceptedMimeTypes() {
    return Arrays.asList(
        MimeTypeApi.CSS_MIMETYPE,
        MimeTypeApi.HTML_MIMETYPE,
        MimeTypeApi.XML_MIMETYPE,
        MimeTypeApi.JSON_MIMETYPE,
        MimeTypeApi.CSV_MIMETYPE);
  }

  @Override
  public List<String> getTargetMimeTypes() {
    return Arrays.asList(MimeTypeApi.TXT_MIMETYPE);
  }

  @Override
  protected BinaryData convertInternal(BinaryContentData content, String toMimeType,
      Map<String, Object> parameters) {
    try {
      BinaryDataObject dataObject =
          objectApi.loadLatest(content.getDataUri()).getObject(BinaryDataObject.class);
      return BinaryData.of(dataObject.getBinaryData().inputStream());
    } catch (Exception e) {
      throw new IllegalStateException("Error while converting docx file to pdf.", e);
    }
  }

}
