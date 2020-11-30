package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import com.vaadin.flow.component.textfield.TextField;

public class DynamicFilterOperationOneFieldUI extends DynamicFilterOperationUI {

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

  @Override
  public List<String> getPossibleOperations() {
    // TODO Auto-generated method stub
    return null;
  }

}
