package org.smartbit4all.ui.vaadin.components.document;

import org.smartbit4all.ui.api.components.document.editing.DocumentViewProcessEditing;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;

public class DocumentViewerDialog extends Dialog {

  private Icon closeIcon;
  private DocumentViewUI documentViewerUI;

  public DocumentViewerDialog(DocumentViewProcessEditing processEditing) {
    setWidth("70%");
    setHeightFull();

    closeIcon = createCloseIcon();

    documentViewerUI = createDocumentViewerUI(processEditing);

    FlexLayout layout = createLayout(closeIcon, documentViewerUI);
    add(layout);
  }

  public void removeCloseIcon() {
    removeAll();
    FlexLayout layout = createLayout(documentViewerUI);
    documentViewerUI.setHeightFull();
    add(layout);
  }

  private FlexLayout createLayout(Component... components) {
    FlexLayout layout = new FlexLayout(components);
    layout.setFlexDirection(FlexDirection.COLUMN);
    layout.setWidthFull();
    layout.setHeightFull();
    return layout;
  }

  private DocumentViewUI createDocumentViewerUI(DocumentViewProcessEditing processEditing) {
    DocumentViewUI documentViewUI = new DocumentViewUI(processEditing);
    documentViewUI.setHeight("95%");
    documentViewUI.setWidthFull();
    return documentViewUI;
  }

  private Icon createCloseIcon() {
    Icon icon = new Icon(VaadinIcon.CLOSE);
    icon.getStyle().set("margin-bottom", "20px");
    icon.getStyle().set("align-self", "flex-end");
    icon.addClickListener(click -> close());
    return icon;
  }

}
