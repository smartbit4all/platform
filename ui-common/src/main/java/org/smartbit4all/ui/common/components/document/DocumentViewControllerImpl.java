package org.smartbit4all.ui.common.components.document;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import org.smartbit4all.api.mimetype.DisplayMode;
import org.smartbit4all.api.mimetype.MimeTypeHandler;
import org.smartbit4all.types.binarydata.BinaryData;
import org.smartbit4all.ui.common.controller.UIController;

/**
 * The basic implementation of {@link DocumentViewController}. If a {@link DocumentView} is set,
 * decides with the help of the {@link MimeTypeHandler} whether in one or more images or in plain
 * text should be displayed the document, and according to that, shows the required panels in the
 * viewer.
 */
public class DocumentViewControllerImpl implements DocumentViewController, UIController {

  private DocumentView ui;
  private MimeTypeHandler mimeTypeHandler;
  private List<BinaryData> images = new LinkedList<>(); // store imageWithAlts instead?
  private String text = null;
  private int actualPageIndex = 0;

  public DocumentViewControllerImpl() {
  }

  // will it be autowired?
  @Override
  public void setMimeTypeHandler(MimeTypeHandler mimeTypeHandler) {
    this.mimeTypeHandler = mimeTypeHandler;
  }

  @Override
  public void setUI(DocumentView documentView) {
    ui = documentView;
  }

  @Override
  public MimeTypeHandler getMimeTypeHandler() {
    return mimeTypeHandler;
  }

  /**
   * Decides with the help of the {@link MimeTypeHandler} whether in images or in plain text should
   * be displayed the document, and according to that, converts the given data to the correct
   * format, then creates and shows the required panels with the {@link DocumentView}.
   */
  @Override
  public void setDocument(BinaryData document, URI uri) {
    if (mimeTypeHandler.getDisplayMode() == DisplayMode.IMAGE) {
      images = mimeTypeHandler.getImages(document, uri);
//      ui.createImageUI();
//      showNavPanel();
//      showThumbnails();
//      showPage(0);
    } else if (mimeTypeHandler.getDisplayMode() == DisplayMode.TEXT) {
      text = mimeTypeHandler.getText(document);
//      ui.createTextUI();
//      ui.displayText(text);
    }
    ui.createUI(images.size() > 1);
    ui.displayData();
  }

  /**
   * Displays the current document's page with the same index as the given parameter, and changes to
   * the correct value the label showing the actual page index. Enables and disables the required
   * navigation buttons if it is necessary.
   */
  @Override
  public void showPage(int pageIndex) {
    actualPageIndex = pageIndex;
    String alt = (pageIndex + 1) + ".png";
    ui.displayImage(new ImageWithAlt(images.get(actualPageIndex).inputStream(), alt, alt));
    ui.setActualPageLabel(String.valueOf(actualPageIndex + 1));
    if (actualPageIndex == 0) {
      ui.setLeftArrowEnable(false);
      ui.setRightArrowEnable(true);
    } else if (actualPageIndex == images.size() - 1) {
      ui.setLeftArrowEnable(true);
      ui.setRightArrowEnable(false);
    } else {
      ui.setLeftArrowEnable(true);
      ui.setRightArrowEnable(true);
    }
  }

  /**
   * Converts the given String value to integer, if it is successful, calls the same named method
   * with integer parameter, otherwise resets the label showing the actual page index to the current
   * correct value.
   */
  @Override
  public void showPage(String pageIndex) {
    try {
      int newPageIndex = Integer.parseInt(pageIndex) - 1;
      if (newPageIndex >= 0 && newPageIndex <= images.size() - 1) {
        actualPageIndex = newPageIndex;
        showPage(actualPageIndex);
      } else {
        ui.setActualPageLabel(String.valueOf(actualPageIndex + 1));
      }
    } catch (NumberFormatException e) {
      ui.setActualPageLabel(String.valueOf(actualPageIndex + 1));
    }
  }

  @Override
  public void showNextPage() {
    showPage(actualPageIndex + 1);
  }

  @Override
  public void showPrevPage() {
    showPage(actualPageIndex - 1);
  }

  /**
   * If the document consists of more than one page, with the {@link DocumentView} creates images
   * from stored BinaryData's InputStreams and created filenames, then displays them in small
   * pictures next to the current big page shown. Otherwise removes the previous shown pictures from
   * that panel.
   */
  @Override
  public void showThumbnails() {
    if (images.size() > 1) {
      List<ImageWithAlt> byteImageWithAlts = new LinkedList<>();
      for (int i = 0; i < images.size(); ++i) {
        String alt = (i + 1) + ".png";
        ImageWithAlt byteImageWithAlt = new ImageWithAlt(images.get(i).inputStream(), alt, alt);
        byteImageWithAlts.add(byteImageWithAlt);
      }
      ui.setThumbnails(byteImageWithAlts);
//      ui.setThumbnailButtonVisible(true);
    } else {
      ui.setThumbnailsVisible(false);
      ui.setThumbnailButtonVisible(false);
    }
  }

  /**
   * If the document consists of more than one page, shows the total page count in the navigation
   * panel, otherwise hides the entire panel.
   */
  @Override
  public void showNavPanel() {
    if (images.size() <= 1)
      ui.setNavPanelVisible(false);
    else
      ui.displayAllPageCount(images.size());
  }

  @Override
  public void showTextArea() {
    ui.displayText(text);
  }

}
