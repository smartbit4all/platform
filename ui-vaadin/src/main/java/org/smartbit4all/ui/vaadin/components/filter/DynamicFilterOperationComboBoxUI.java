package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.value.bean.Value;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.textfield.TextField;

public class DynamicFilterOperationComboBoxUI extends DynamicFilterOperationUI{
  ComboBox<Value> comboBox;
  private String filterName;
  
  public DynamicFilterOperationComboBoxUI(String filterName) {
    this.filterName = filterName;
    comboBox = new ComboBox<>();
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
  
  
}
