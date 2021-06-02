package org.smartbit4all.api.mimetype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.smartbit4all.types.binarydata.BinaryData;

public class MimeTypeHandlerText implements MimeTypeHandler {

  private final List<String> ACCEPTED_MIMETYPES = new ArrayList<>(Arrays.asList("text/plain", "text/html"));

  private DisplayMode displayMode;

  public MimeTypeHandlerText() {
    this.displayMode = DisplayMode.TEXT;
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
    return null;
  }

  @Override
  public String getText(BinaryData document) {
    try {
      StringBuilder textBuilder = new StringBuilder();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(document.inputStream()))) {
        int c = 0;
        while ((c = br.read()) != -1) {
          textBuilder.append((char) c);
        }
      }
      return textBuilder.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
