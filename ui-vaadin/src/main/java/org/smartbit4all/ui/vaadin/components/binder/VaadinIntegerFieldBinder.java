package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.BiConsumer;
import org.smartbit4all.ui.common.filter.AbstractUIState;
import com.vaadin.flow.component.textfield.IntegerField;

public class VaadinIntegerFieldBinder<S extends AbstractUIState> extends VaadinInputBinder<S, Integer>{

  protected IntegerField integerField;

  public VaadinIntegerFieldBinder(IntegerField integerField, S uiState, BiConsumer<S, Integer> setter, String propertyName) {
    super(uiState, setter, propertyName);
    this.integerField = integerField;
    registerIntegerFieldValueChangeListener();
  }

  protected void onUIStateChanged(Integer value) {
    Integer currentValue = integerField.getValue();

    if (currentValue == null && value == null) {
      return;
    } else if (currentValue != null && value == null) {
      integerField.setValue(null);
    } else if ((currentValue == null && value != null) || !currentValue.equals(value)) {
      integerField.setValue(value);
    }
  }

  protected void registerIntegerFieldValueChangeListener() {
    integerField.addValueChangeListener(event -> setUIState(event.getValue()));
  }
}
