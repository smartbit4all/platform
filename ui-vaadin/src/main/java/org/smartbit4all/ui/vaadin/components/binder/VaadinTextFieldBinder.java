package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.BiConsumer;
import org.smartbit4all.ui.common.filter.AbstractUIState;
import com.vaadin.flow.component.textfield.TextField;

public class VaadinTextFieldBinder<S extends AbstractUIState> extends VaadinInputBinder<S, String>{

  protected TextField textField;

  public VaadinTextFieldBinder(TextField textField, S uiState, BiConsumer<S, String> setter, String propertyName) {
    super(uiState, setter, propertyName);
    this.textField = textField;
    registerTextFieldValueChangeListener();
  }

  protected void onUIStateChanged(String value) {
    String currentValue = textField.getValue();

    if ("".equals(currentValue) && value == null) {
      return;
    } else if (!"".equals(currentValue) && value == null) {
      textField.setValue("");
    } else if ((currentValue == null && value != null) || !currentValue.equals(value)) {
      textField.setValue(String.valueOf(value));
    }
  }

  protected void registerTextFieldValueChangeListener() {
    textField.addValueChangeListener(event -> setUIState(event.getValue()));
  }
}
