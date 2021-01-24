package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.BiConsumer;
import org.smartbit4all.ui.common.filter.AbstractUIState;

public abstract class VaadinInputBinder<S extends AbstractUIState, T> {

  protected S uiState;
  
  protected BiConsumer<S, T> setter;
  
  protected String propertyName;
  
  public VaadinInputBinder(S uiState, BiConsumer<S, T> setter, String propertyName) {
    super();
    this.uiState = uiState;
    this.setter = setter;
    this.propertyName = propertyName;
    
    subscribeToUIEvent();
  }

  protected void subscribeToUIEvent() {
    uiState.events().propertyChangeEvent().subscribe().onPropertyChange(propertyName)
        .add(propertyChange -> onUIStateChanged((T) propertyChange.getValue()));    
  }
  
  protected abstract void onUIStateChanged(T value);


  protected void setUIState(T value) {
    setter.accept(uiState, value);
  }
}
