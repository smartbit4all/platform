package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.Consumer;
import org.smartbit4all.ui.common.filter.AbstractUIState;
import com.vaadin.flow.component.button.Button;

public class VaadinButtonBinder<S extends AbstractUIState> {

  protected Button button;

  protected S uiState;

  protected Consumer<S> function;

  public VaadinButtonBinder(Button button, S uiState, Consumer<S> function) {
    this.button = button;
    this.uiState = uiState;
    this.function = function;
    registerButtonClickListener();
  }

  protected void registerButtonClickListener() {
    button.addClickListener(event -> function.accept(uiState));
  }

}
