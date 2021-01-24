package org.smartbit4all.ui.vaadin.components.binder;

import java.lang.reflect.Method;
import org.smartbit4all.ui.common.filter.AbstractUIState;
import com.vaadin.flow.component.button.Button;

public class VaadinButtonBinder {

  protected Button button;

  protected AbstractUIState uiState;

  protected String opertation;

  public VaadinButtonBinder(Button button, AbstractUIState uiState, String opertation) {
    super();
    this.button = button;
    this.uiState = uiState;
    this.opertation = opertation;

    registerComboBoxChangeListener();
  }

  protected void registerComboBoxChangeListener() {
    button.addClickListener(event -> callUIState());
  }

  protected void callUIState() {
    try {
      Method method = uiState.getClass().getMethod(opertation);
      method.invoke(uiState);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
