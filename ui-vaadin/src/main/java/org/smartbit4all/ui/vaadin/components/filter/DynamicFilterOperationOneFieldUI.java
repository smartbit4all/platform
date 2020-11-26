package org.smartbit4all.ui.vaadin.components.filter;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

public class DynamicFilterOperationOneFieldUI extends DynamicFilterOperationUI{

  private TextField textField;
  private String filterName;

  public DynamicFilterOperationOneFieldUI(String filterName) {
    addClassName("dynamic-filter-onefield");
    this.filterName = filterName;
    textField = new TextField();
    add(textField);
  }
  
  public String getFilterName() {
    return filterName;
  }
  

  @Override
  public void setPlaceholder(String placeHolderText) {
    textField.setPlaceholder(placeHolderText);
    
  }
  
}
