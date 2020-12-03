package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.value.bean.Value;
import com.vaadin.flow.component.combobox.ComboBox;

public class FilterOperationComboBoxUI extends FilterOperationUI {
  ComboBox<Value> comboBox;
  private String filterName;

  public FilterOperationComboBoxUI(String filterName) {
    this.filterName = filterName;
    comboBox = new ComboBox<>();
    comboBox.addClassName("filter-combobox");
    comboBox.setItemLabelGenerator(v -> v.getValue());
    add(comboBox);
  }

  public void setItems(List<Value> items) {
    comboBox.setItems(items);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    comboBox.setPlaceholder(placeHolderText);
  }

  @Override
  public String getFilterName() {
    return filterName;
  }

  @Override
  public List<String> getPossibleOperations() {
    // TODO Auto-generated method stub
    return null;
  }


}
