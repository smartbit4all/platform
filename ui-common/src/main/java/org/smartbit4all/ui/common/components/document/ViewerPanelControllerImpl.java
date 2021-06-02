package org.smartbit4all.ui.common.components.document;

import java.net.URI;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.api.mimetype.MimeTypeHandler;
import org.smartbit4all.types.binarydata.BinaryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * The basic implementation of {@link ViewerPanelController}. If a {@link ViewerPanelView} is
 * set, decides whether the given document can be displayed or not with the help of
 * {@link MimeTypeApi}, then displays the content if a {@link MimeTypeHandler} found to handle it or
 * shows the proper warning message. If the content can be displayed, sets the found handler and the
 * document in the DocumentController.
 */
@Service
@Scope("prototype")
public class ViewerPanelControllerImpl implements ViewerPanelController {

  @Autowired
  private DocumentViewController controller;

  @Autowired
  private MimeTypeApi mimeTypeApi;

  private ViewerPanelView ui;

  @Override
  public void setUI(ViewerPanelView view) {
    this.ui = view;
  }

  @Override
  public void showContent(BinaryData content, String mimeType, URI uri) {
    MimeTypeHandler handler = mimeTypeApi.getHandler(mimeType);
    if (handler == null) {
      ui.displayNoAvailMessage();
    } else {
      controller.setMimeTypeHandler(handler);
      ui.displayViewer();
      controller.setDocument(content, uri);
    }
  }

  @Override
  public void showNotSelectedEntryMessage() {
    ui.displayNotSelectedEntryMessage();
  }

  @Override
  public DocumentViewController getDocumentViewController() {
    return controller;
  }

}
