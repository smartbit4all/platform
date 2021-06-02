package org.smartbit4all.ui.common.components.document;

import java.net.URI;
import org.smartbit4all.api.mimetype.MimeTypeHandler;
import org.smartbit4all.types.binarydata.BinaryData;
import org.springframework.scheduling.annotation.Async;

/**
 * Handles the panel used for viewing documents. After the {@link DocumentView} is set, this
 * controller helps to decide what to show in it. Displays text-only data in a plain text area, or
 * other documents in one big image if the document is only one page long, otherwise all of the
 * pages are shown as smaller images (thumbnails) next to the big image. In the image version
 * navigation and zoom in/out buttons are shown too. The {@link MimeTypeHandler} helps to decide
 * what type of display is required, and according to that, converts the content into images or
 * text, which will be displayed after that.
 */
public interface DocumentViewController {
  @Async
  void showNextPage();

  @Async
  void showPrevPage();

  @Async
  void showThumbnails();

  @Async
  void showPage(int pageIndex);

  @Async
  void showPage(String pageIndex);

  @Async
  void setUI(DocumentView documentView);

  @Async
  void setMimeTypeHandler(MimeTypeHandler mimeTypeHandler);

  @Async
  void showNavPanel();

  @Async
  void setDocument(BinaryData document, URI uri);

  @Async
  void showTextArea();

  MimeTypeHandler getMimeTypeHandler();
}
