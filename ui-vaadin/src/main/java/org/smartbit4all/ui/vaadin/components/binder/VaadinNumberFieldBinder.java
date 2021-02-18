package org.smartbit4all.ui.vaadin.components.binder;

import org.smartbit4all.api.object.ObjectEditing;
import org.smartbit4all.api.object.PropertyChange;
import com.vaadin.flow.component.textfield.NumberField;

public class VaadinNumberFieldBinder extends VaadinInputBinder{

  protected NumberField numberField;

  public VaadinNumberFieldBinder(NumberField numberField, ObjectEditing editing, String path) {
    super(editing, path);
    this.numberField = numberField;
    registerNumberFieldValueChangeListener();
  }

  protected void onUIStateChanged(PropertyChange value) {
    Double currentValue = numberField.getValue();
    Long newValue = (Long) value.getNewValue();

    if (currentValue == null && newValue == null) {
      return;
    } else if (currentValue != null && newValue == null) {
      numberField.setValue(null);
    } else if ((currentValue == null && newValue != null) || !currentValue.equals(newValue)) {
      numberField.setValue(newValue.doubleValue());
    }
  }

  protected void registerNumberFieldValueChangeListener() {
    numberField.addValueChangeListener(event -> setUIState(event.getValue().longValue()));
  }
}
