package org.smartbit4all.ui.vaadin.components.storage.history;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;

public class ObjectHistoryDetailDialog extends Dialog {

  public  ObjectHistoryDetailDialog(Component component) {
    setWidth("70%");
    setHeight("70%");
    setResizable(true);
    add(component);
  }
}
