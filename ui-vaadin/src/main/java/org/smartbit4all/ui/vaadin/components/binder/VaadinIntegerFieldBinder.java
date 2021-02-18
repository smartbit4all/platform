package org.smartbit4all.ui.vaadin.components.binder;

import org.smartbit4all.api.object.ObjectEditing;
import org.smartbit4all.api.object.PropertyChange;
import com.vaadin.flow.component.textfield.IntegerField;

public class VaadinIntegerFieldBinder extends VaadinInputBinder {

  protected IntegerField integerField;

  public VaadinIntegerFieldBinder(IntegerField integerField, ObjectEditing editing,
      String path) {
    super(editing, path);
    this.integerField = integerField;
    registerIntegerFieldValueChangeListener();
  }

  protected void onUIStateChanged(PropertyChange value) {
    Integer currentValue = integerField.getValue();
    Integer newValue = (Integer) value.getNewValue();

    if (currentValue == null && newValue == null) {
      return;
    } else if (currentValue != null && newValue == null) {
      integerField.setValue(null);
    } else if ((currentValue == null && newValue != null) || !currentValue.equals(newValue)) {
      integerField.setValue(newValue);
    }
  }

  protected void registerIntegerFieldValueChangeListener() {
    integerField.addValueChangeListener(event -> setUIState(event.getValue()));
  }
}
