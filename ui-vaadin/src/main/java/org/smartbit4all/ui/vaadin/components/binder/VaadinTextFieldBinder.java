package org.smartbit4all.ui.vaadin.components.binder;

import org.smartbit4all.api.object.ObjectEditing;
import org.smartbit4all.api.object.PropertyChange;
import com.vaadin.flow.component.textfield.TextField;

public class VaadinTextFieldBinder extends VaadinInputBinder {

  protected TextField textField;

  public VaadinTextFieldBinder(TextField textField, ObjectEditing editing, String path) {
    super(editing, path);
    this.textField = textField;
    registerTextFieldValueChangeListener();
  }

  protected void onUIStateChanged(PropertyChange value) {
    String currentValue = textField.getValue();
    String newValue = (String) value.getNewValue();

    if ("".equals(currentValue) && newValue == null) {
      return;
    } else if (!"".equals(currentValue) && newValue == null) {
      textField.setValue("");
    } else if ((currentValue == null && newValue != null) || !currentValue.equals(newValue)) {
      textField.setValue(newValue);
    }
  }

  protected void registerTextFieldValueChangeListener() {
    textField.addValueChangeListener(event -> setUIState(event.getValue()));
  }
}
