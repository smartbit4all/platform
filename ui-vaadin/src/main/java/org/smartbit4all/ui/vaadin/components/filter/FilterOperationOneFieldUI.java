package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import com.vaadin.flow.component.textfield.TextField;

public class FilterOperationOneFieldUI extends FilterOperationUI {

  private TextField textField;
  private String filterName;

  public FilterOperationOneFieldUI(String filterName) {
    addClassName("filter-onefield");
    this.filterName = filterName;
    textField = new TextField();
    add(textField);
  }

  @Override
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
