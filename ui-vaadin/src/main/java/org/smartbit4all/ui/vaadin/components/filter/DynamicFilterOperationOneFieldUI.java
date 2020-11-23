package org.smartbit4all.ui.vaadin.components.filter;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

public class DynamicFilterOperationOneFieldUI extends Div {

  private TextField text;
  private String label;

  public DynamicFilterOperationOneFieldUI(String label) {
    addClassName("dynamic-filter-onefield");
    this.label = label;
    label = getTranslation(label);
    text = new TextField(label);
    add(text);
  }
  
  public String getLabel() {
    return label;
  }
  
  public void setLabel(String label) {
    text.setLabel(label);  
  }
  
  public TextField getTextField() {
    return text;
  }
  
}
