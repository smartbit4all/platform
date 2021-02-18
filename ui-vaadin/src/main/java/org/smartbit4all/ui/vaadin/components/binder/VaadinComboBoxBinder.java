package org.smartbit4all.ui.vaadin.components.binder;

import org.smartbit4all.api.object.ObjectEditing;
import org.smartbit4all.api.object.PropertyChange;
import com.vaadin.flow.component.combobox.ComboBox;

public class VaadinComboBoxBinder<T> extends VaadinInputBinder {

  protected ComboBox<T> comboBox;

  public VaadinComboBoxBinder(ComboBox<T> comboBox, ObjectEditing editing, String path) {
    super(editing, path);
    this.comboBox = comboBox;
    registerComboBoxValueChangeListener();
  }

  @Override
  protected void onUIStateChanged(PropertyChange value) {
    T currentValue = comboBox.getValue();
    T newValue = (T) value.getNewValue();

    if (currentValue == null && newValue == null) {
      return;
    }

    if ((currentValue == null && newValue != null) || !currentValue.equals(newValue)) {
      comboBox.setValue(newValue);
    }
  }

  protected void registerComboBoxValueChangeListener() {
    comboBox.addValueChangeListener(event -> setUIState(event.getValue()));
  }

}
