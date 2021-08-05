package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.documentview.bean.DisplayMode;
import org.smartbit4all.types.binarydata.BinaryData;

/**
 * A handler is special API for manipulating the given kind of documents. We can extract data from
 * this.
 * 
 * @author András Palló
 */
public interface MimeTypeHandler {

  List<String> getAcceptedMimeTypes();

  DisplayMode getDisplayMode();

  List<BinaryData> getImages(BinaryData document, URI uri);

  String getText(BinaryData document);

}
