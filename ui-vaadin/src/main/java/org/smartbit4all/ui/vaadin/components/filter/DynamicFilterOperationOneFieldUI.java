package org.smartbit4all.ui.vaadin.components.filter;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

public class DynamicFilterOperationOneFieldUI extends Div {

  private TextField text;

  public DynamicFilterOperationOneFieldUI(String label) {
    addClassName("dynamic-filter-onefield");
    text = new TextField(label);
    add(text);
  }

}
