package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.value.bean.Value;
import com.vaadin.flow.component.combobox.ComboBox;

public class FilterOperationComboBoxUI extends FilterOperationUI {
  ComboBox<Value> comboBox;

  public FilterOperationComboBoxUI(List<Value> possibleValues) {
    comboBox = new ComboBox<>();
    comboBox.addClassName("filter-combobox");
    comboBox.setItemLabelGenerator(v -> v.getValue());
    add(comboBox);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    comboBox.setPlaceholder(placeHolderText);
  }

}
