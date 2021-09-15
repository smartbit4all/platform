package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.documentview.bean.DisplayMode;

public class MimeTypeHandlerImage implements MimeTypeHandler {

  private final List<String> ACCEPTED_MIMETYPES =
      new ArrayList<>(Arrays.asList(MimeTypeApi.PNG_MIMETYPE, MimeTypeApi.JPEG_MIMETYPE,
          MimeTypeApi.GIF_MIMETYPE));

  private DisplayMode displayMode;

  public MimeTypeHandlerImage() {
    this.displayMode = DisplayMode.IMAGE;
  }

  @Override
  public List<String> getAcceptedMimeTypes() {
    return ACCEPTED_MIMETYPES;
  }

  @Override
  public DisplayMode getDisplayMode() {
    return displayMode;
  }

  @Override
  public List<BinaryData> getImages(BinaryData document, URI uri) {
    return new ArrayList<>(Arrays.asList(document));
  }

  @Override
  public String getText(BinaryData document) {
    return null;
  }
}
