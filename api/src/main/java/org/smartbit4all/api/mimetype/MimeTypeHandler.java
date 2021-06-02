package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.util.List;
import org.smartbit4all.types.binarydata.BinaryData;

public interface MimeTypeHandler {
  
  List<String> getAcceptedMimeTypes();
  
  DisplayMode getDisplayMode();
  
  List<BinaryData> getImages(BinaryData document, URI uri);
  
  String getText(BinaryData document);
  
}
