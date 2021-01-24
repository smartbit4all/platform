package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.BiConsumer;
import org.smartbit4all.ui.common.filter.AbstractUIState;
import com.vaadin.flow.component.textfield.NumberField;

public class VaadinNumberFieldBinder<S extends AbstractUIState> extends VaadinInputBinder<S, Long>{

  protected NumberField numberField;

  public VaadinNumberFieldBinder(NumberField numberField, S uiState, BiConsumer<S, Long> setter, String propertyName) {
    super(uiState, setter, propertyName);
    this.numberField = numberField;
    registerNumberFieldValueChangeListener();
  }

  protected void onUIStateChanged(Long value) {
    Double currentValue = numberField.getValue();

    if (currentValue == null && value == null) {
      return;
    } else if (currentValue != null && value == null) {
      numberField.setValue(null);
    } else if ((currentValue == null && value != null) || !currentValue.equals(value)) {
      numberField.setValue(value.doubleValue());
    }
  }

  protected void registerNumberFieldValueChangeListener() {
    numberField.addValueChangeListener(event -> setUIState(event.getValue().longValue()));
  }
}
