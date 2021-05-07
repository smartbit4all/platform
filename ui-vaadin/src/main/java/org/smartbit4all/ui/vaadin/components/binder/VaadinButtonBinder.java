package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.Consumer;
import org.smartbit4all.core.object.ObjectEditing;
import com.vaadin.flow.component.button.Button;

public class VaadinButtonBinder<S extends ObjectEditing> {

  protected Button button;

  protected S editing;

  protected Consumer<S> function;

  public VaadinButtonBinder(Button button, S editing, Consumer<S> function) {
    this.button = button;
    this.editing = editing;
    this.function = function;
    registerButtonClickListener();
  }

  protected void registerButtonClickListener() {
    button.addClickListener(event -> function.accept(editing));
  }

}
