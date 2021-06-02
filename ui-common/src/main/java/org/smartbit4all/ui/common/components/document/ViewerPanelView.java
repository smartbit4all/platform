package org.smartbit4all.ui.common.components.document;

/**
 * The holder of the panel used for viewing documents. Can display the viewer, which handles the
 * document appearance, and is able to display warning messages too if there is no available content
 * to show.
 */

public interface ViewerPanelView {
  void displayNoAvailMessage();

  void displayViewer();

  void displayNotSelectedEntryMessage();
}
