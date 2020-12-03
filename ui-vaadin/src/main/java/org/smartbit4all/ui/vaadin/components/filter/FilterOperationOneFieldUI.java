package org.smartbit4all.ui.vaadin.components.filter;

import com.vaadin.flow.component.textfield.TextField;

public class FilterOperationOneFieldUI extends FilterOperationUI {

  private TextField textField;

  public FilterOperationOneFieldUI() {
    addClassName("filter-onefield");
    textField = new TextField();
    add(textField);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    textField.setPlaceholder(placeHolderText);
  }
}
