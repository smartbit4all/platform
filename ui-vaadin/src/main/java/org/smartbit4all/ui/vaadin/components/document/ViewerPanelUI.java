package org.smartbit4all.ui.vaadin.components.document;

import org.smartbit4all.ui.common.components.document.ViewerPanelController;
import org.smartbit4all.ui.common.components.document.ViewerPanelView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * The Vaadin based implementation of {@link ViewerPanelView}. Can create and display a
 * {@link DocumentViewUI} and links the {@link DocumentViewController} to it when created. Can also
 * display warning messages if there is no available content to show.
 */
public class ViewerPanelUI extends FlexLayout implements ViewerPanelView {

  private ViewerPanelController controller;

  private HorizontalLayout noAvailMessage;

  private HorizontalLayout notSelectedMessage;

  public ViewerPanelUI(ViewerPanelController controller) {
    this.controller = controller;
    controller.setUI(this);
    createNoAvailableMessage();
    createNotSelectedEntryMessage();
  }

  private void createNotSelectedEntryMessage() {
    Label notSelectedEntryMsgLabel = new Label(getTranslation("main.selectanitemtoinsight"));
    notSelectedEntryMsgLabel.getStyle().set("margin", "auto");
    notSelectedMessage = new HorizontalLayout(notSelectedEntryMsgLabel);
    notSelectedMessage.setSizeFull();
  }

  private void createNoAvailableMessage() {
    Label noAvailMessageLabel = new Label(getTranslation("main.noinsight"));
    noAvailMessageLabel.getStyle().set("margin", "auto");
    noAvailMessage = new HorizontalLayout(noAvailMessageLabel);
    noAvailMessage.setSizeFull();
  }

  @Override
  public void displayNoAvailMessage() {
    removeAll();
    add(noAvailMessage);
  }

  @Override
  public void displayNotSelectedEntryMessage() {
    removeAll();
    add(notSelectedMessage);

  }

  @Override
  public void displayViewer() {
    removeAll();
    add(new DocumentViewUI(controller.getDocumentViewController()));
  }

}
