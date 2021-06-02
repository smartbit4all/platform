package org.smartbit4all.ui.common.components.document;

import java.util.List;

/**
 * The panel used for viewing documents. Can display the content in a plain text area or in one big
 * and other small images (thumbnails). Besides that, in the image version it shows the viewer's
 * buttons and handles their on-click events, that way it is possible to navigate through the pages
 * and zoom in/out in the big image.
 */
public interface DocumentView {
  void displayImage(ImageWithAlt image);

  void setThumbnails(List<ImageWithAlt> thumbnails);

  void setLeftArrowEnable(boolean enabled);

  void setRightArrowEnable(boolean enabled);

  void displayAllPageCount(int allPageCount);

  void setNavPanelVisible(boolean isVisible);

  void setActualPageLabel(String actualPage);

  void setThumbnailsVisible(boolean isVisible);

  void setThumbnailButtonVisible(boolean isvisible);

  void displayData();

  void createUI(boolean createThumbnailsPanel);

  void displayText(String text);
}
