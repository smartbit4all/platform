package org.smartbit4all.ui.vaadin.components.binder;

import org.smartbit4all.ui.common.filter.AbstractUIState;
import com.vaadin.flow.component.html.Label;

public class VaadinLabelBinder {

  protected Label label;

  protected AbstractUIState uiState;

  protected String property;

  public VaadinLabelBinder(Label label, AbstractUIState uiState, String property) {
    super();
    this.label = label;
    this.uiState = uiState;
    this.property = property;

    subscribeToUIEvent();
  }

  protected void subscribeToUIEvent() {
    uiState.events().propertyChangeEvent().subscribe().onPropertyChange(property)
        .add(propertyChange -> onUIStateChanged(propertyChange.getValue()));
  }

  protected void onUIStateChanged(Object value) {
    String currentValue = label.getText();

    if (currentValue == null && value == null) {
      return;
    }
    
    if ((currentValue == null && value != null) || !currentValue.equals(value)) {
      label.setText((String) value);
    }
  }

}
