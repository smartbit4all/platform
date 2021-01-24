package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.BiConsumer;
import org.smartbit4all.ui.common.filter.AbstractUIState;
import com.vaadin.flow.component.combobox.ComboBox;

public class VaadinComboBoxBinder<S extends AbstractUIState, T> extends VaadinInputBinder<S, T>{

  protected ComboBox<T> comboBox;

  protected AbstractUIState uiState;

  protected String property;

  public VaadinComboBoxBinder(ComboBox<T> comboBox, S uiState, BiConsumer<S, T> setter, String propertyName) {
    super(uiState, setter, propertyName);
    this.comboBox = comboBox;
    registerComboBoxValueChangeListener();
  }

  @Override
  protected void onUIStateChanged(T value) {
    T currentValue = comboBox.getValue();

    if (currentValue == null && value == null) {
      return;
    }
    
    if ((currentValue == null && value != null) || !currentValue.equals(value)) {
      comboBox.setValue(value);
    }    
  }

  protected void registerComboBoxValueChangeListener() {
    comboBox.addValueChangeListener(event -> setUIState(event.getValue()));    
  }

}
