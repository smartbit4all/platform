package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.Function;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.ui.binder.AbstractBinder;
import com.vaadin.flow.component.HasText;

public class VaadinHasTextBinder extends AbstractBinder {

  protected HasText label;

  protected ObservableObject observableObject;

  protected String[] propertyPath;

  private Function<Object, String> converter;

  public VaadinHasTextBinder(HasText label, ObservableObject observableObject,
      Function<Object, String> converter, String... propertyPath) {
    super();
    this.label = label;
    this.observableObject = observableObject;
    this.propertyPath = propertyPath;
    this.converter = converter;

    subscribeToUIEvent();
  }

  protected void subscribeToUIEvent() {
    disposable = observableObject.onPropertyChange(value -> onUIStateChanged(value), propertyPath);
  }

  protected void onUIStateChanged(PropertyChange value) {
    String currentValue = label.getText();
    Object newValue = value.getNewValue();

    if (converter != null) {
      newValue = converter.apply(newValue);
    }
    newValue = newValue == null ? "" : newValue;
    currentValue = currentValue == null ? "" : currentValue;

    if (!currentValue.equals(newValue)) {
      label.setText(newValue.toString());
    }

  }

}
