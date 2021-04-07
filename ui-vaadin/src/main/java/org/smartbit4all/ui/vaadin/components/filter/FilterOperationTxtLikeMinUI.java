package org.smartbit4all.ui.vaadin.components.filter;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.ui.common.filter.FilterValueChangeListener;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class FilterOperationTxtLikeMinUI extends FilterOperationUI {
  
  private FilterValueChangeListener filterValueChangeListener;

  private TextField textField;
  private Binder<String> binder;
  private String value;
  
  public FilterOperationTxtLikeMinUI(FilterValueChangeListener filterValueChangeListener) {
    this.filterValueChangeListener = filterValueChangeListener;
    addClassName("filter-onefield");
    addClassName("filter-txt-like-min");
    textField = new TextField();
    textField.addValueChangeListener(valueChangeListener());
    textField.setClearButtonVisible(true);
    textField.addClassName("filter-txt-like-min-txt");
    binder = new Binder<>();
    binder.forField(textField)
        .withValidator(element -> element.length() >= 3, textField.getTranslation("textfield.minimum.length"))
        .bind((s) -> value, (s, v) -> {
          this.value = v;
        });

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
    textField.setPlaceholder(placeHolderText);
  }

  @Override
  public void setValues(FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3) {
    if (value1 == null || value1.getValue() == null) {
      textField.setValue(null);
      return;
    }
    textField.setValue(value1.getValue().toUpperCase());
    
  }

  @Override
  public void setSelection(List<URI> list) {
    // TODO Auto-generated method stub
    
  }

}
