package org.smartbit4all.ui.vaadin.components.filter;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.ui.common.filter.FilterValueChangeListener;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.textfield.TextField;

public class FilterOperationTxtLikeUI extends FilterOperationUI {

  private FilterValueChangeListener filterValueChangeListener;

  private TextField textField;

  public FilterOperationTxtLikeUI(FilterValueChangeListener filterValueChangeListener) {
    this.filterValueChangeListener = filterValueChangeListener;
    addClassName("filter-onefield");
    textField = new TextField();
    textField.addValueChangeListener(valueChangeListener());

    add(textField);
  }

  private ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> valueChangeListener() {
    return e -> {
      if (e.isFromClient()) {
        String fieldValue = textField.getValue();
        fieldValue = fieldValue == null ? null : fieldValue.toUpperCase();
        FilterOperandValue value1 =
            new FilterOperandValue().type(String.class.getName()).value(fieldValue);
        filterValueChangeListener.filterValueChanged(filterId, value1, null, null);
      }
    };
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValues(FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3) {
    if (value1 == null || value1.getValue() == null) {
      textField.setValue(null);
      return;
    }
    textField.setValue(value1.getValue());

  }

  @Override
  public void setSelection(List<URI> list) {
    // TODO Auto-generated method stub

  }

}
