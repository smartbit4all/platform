package org.smartbit4all.ui.common.components.document;

import java.net.URI;
import org.smartbit4all.types.binarydata.BinaryData;

/**
 * Handles the holder of the panel used for viewing documents. After the {@link ViewerPanelView} is
 * set, this controller helps to decide what to show in it, and whether the given document can be
 * displayed or not, also shows the proper warning message if needed.
 */

public interface ViewerPanelController {
  void showContent(BinaryData content, String mimeType, URI uri);

  void setUI(ViewerPanelView view);

  void showNotSelectedEntryMessage();

  DocumentViewController getDocumentViewController();

}
