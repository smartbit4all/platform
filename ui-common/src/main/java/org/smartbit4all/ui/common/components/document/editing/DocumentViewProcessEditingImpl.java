package org.smartbit4all.ui.common.components.document.editing;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.documentview.bean.DisplayMode;
import org.smartbit4all.api.documentview.bean.DocumentViewProcess;
import org.smartbit4all.api.documentview.bean.ImageWithAlt;
import org.smartbit4all.api.mimetype.MimeTypeHandler;
import org.smartbit4all.api.mimetype.MimeTypeHandlerApi;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.ui.api.components.document.editing.DocumentViewProcessEditing;
import org.smartbit4all.ui.api.viewmodel.ObjectEditingImpl;

/**
 * Handles the panel used for viewing documents in images.
 */
public class DocumentViewProcessEditingImpl extends ObjectEditingImpl
    implements DocumentViewProcessEditing {

  private final ObservableObjectImpl publisher;

  private Map<Class<?>, ApiBeanDescriptor> documentViewDescriptor;

  private DocumentViewProcess processWrapper;

  private MimeTypeHandlerApi mimeTypeHandlerApi;

  private int actualPageIndex = 0;
  private int actualPageWidth;
  private List<BinaryData> images = new ArrayList<>();

  public DocumentViewProcessEditingImpl(Map<Class<?>, ApiBeanDescriptor> documentViewDescriptor,
      MimeTypeHandlerApi mimeTypeHandlerApi, ObservablePublisherWrapper publisherWrapper) {
    super();
    this.documentViewDescriptor = documentViewDescriptor;
    this.mimeTypeHandlerApi = mimeTypeHandlerApi;
    this.publisher = new ObservableObjectImpl(publisherWrapper);

    initDocumentViewProcess();
  }

  @Override
  public ObservableObject process() {
    return publisher;
  }

  private void initDocumentViewProcess() {
    ref = new ApiObjectRef(null, new DocumentViewProcess(), documentViewDescriptor);
    publisher.setRef(ref);
    processWrapper = ref.getWrapper(DocumentViewProcess.class);
  }

  /**
   * With the help of {@link MimeTypeHandlerApi} decides whether the document with the certain
   * format can be displayed or not, and what type of display is required, according to that,
   * converts the content into image(s) or text, which will be displayed in the attached UI. If the
   * document consists of more than one page, it also displays every page as small images
   * (thumbnails) next to the actual page shown.
   */
  @Override
  public synchronized void setDocument(BinaryData document, URI uri, String mimeType) {
    MimeTypeHandler handler = mimeTypeHandlerApi.getHandler(mimeType);
    if (handler == null) {
      showMessage("Nem tartozik előnézet a kiválasztott dokumentum formátumhoz!");
    } else {
      processWrapper.setDisplayMode(handler.getDisplayMode());
      if (handler.getDisplayMode() == DisplayMode.TEXT) {
        String text = handler.getText(document);
        processWrapper.setText(text);
      } else if (handler.getDisplayMode() == DisplayMode.IMAGE) {
        images = handler.getImages(document, uri);
        processWrapper.setPageCount(images.size());
        setPage(0);
        setThumbnails();
      }
    }
    publisher.notifyListeners();
  }

  /**
   * If the document consists of more than one page, it also displays every page as images
   * (thumbnails) next to the actual page shown.
   */
  private void setThumbnails() {
    if (processWrapper.getThumbnails() != null) {
      processWrapper.getThumbnails().clear();
    }
    if (images.size() > 1) {
      List<ImageWithAlt> byteImageWithAlts = new LinkedList<>();
      for (int i = 0; i < images.size(); ++i) {
        String alt = (i + 1) + ".png";
        ImageWithAlt byteImageWithAlt =
            new ImageWithAlt().image(images.get(i)).alt(alt).fileName(alt);
        byteImageWithAlts.add(byteImageWithAlt);
      }
      processWrapper.setThumbnails(byteImageWithAlts);
    }
  }

  /**
   * Displays the current document's page with the same index as the given parameter, and changes to
   * the correct value the label showing the actual page index. Enables and disables the required
   * navigation buttons if it is necessary.
   */
  @Override
  public void setPage(int index) {
    actualPageIndex = index;
    String alt = (index + 1) + ".png";
    processWrapper
        .setMainImage(new ImageWithAlt().image(images.get(actualPageIndex)).alt(alt).fileName(alt));
    processWrapper.setPageIndex(String.valueOf(actualPageIndex + 1));
    if (actualPageIndex == 0) {
      processWrapper.setLeftButtonEnabled(false);
      processWrapper.setRightButtonEnabled(true);
    } else if (actualPageIndex == images.size() - 1) {
      processWrapper.setLeftButtonEnabled(true);
      processWrapper.setRightButtonEnabled(false);
    } else {
      processWrapper.setLeftButtonEnabled(true);
      processWrapper.setRightButtonEnabled(true);
    }
  }

  /**
   * Converts the given String value to integer, if it is successful, calls the same named method
   * with integer parameter, otherwise resets the label showing the actual page index to the current
   * correct value.
   */
  @Override
  public void setPage(String index) {
    try {
      int newPageIndex = Integer.parseInt(index) - 1;
      if (newPageIndex >= 0 && newPageIndex <= images.size() - 1) {
        actualPageIndex = newPageIndex;
        setPage(actualPageIndex);
      } else {
        processWrapper.setPageIndex(String.valueOf(actualPageIndex + 1));
      }
    } catch (NumberFormatException e) {
      processWrapper.setPageIndex(String.valueOf(actualPageIndex + 1));
    }
  }

  @Override
  public void showPrevPage() {
    setPage(actualPageIndex - 1);
  }

  @Override
  public void showNextPage() {
    setPage(actualPageIndex + 1);
  }

  @Override
  public void zoomOut() {
    int newPageWidth = actualPageWidth - 100;
    if (newPageWidth >= 200) {
      actualPageWidth = newPageWidth;
      processWrapper.setZoomValue(actualPageWidth);
    }
  }

  @Override
  public void zoomIn() {
    int newPageWidth = actualPageWidth + 100;
    if (newPageWidth <= 3000) {
      actualPageWidth = newPageWidth;
      processWrapper.setZoomValue(actualPageWidth);
    }
  }

  @Override
  public void showMessage(String message) {
    processWrapper.setMessage(message);
  }

  @Override
  public void displayLoadingSpinner() {
    processWrapper.setLoadingSpinnerDisplayed(true);
  }

  @Override
  public void setActualPageWidth(int pageWidth) {
    actualPageWidth = pageWidth;
  }
}
