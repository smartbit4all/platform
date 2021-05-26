package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.Function;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.utility.PathUtility;
import com.vaadin.flow.component.HasText;

public class VaadinHasTextBinder {

  protected HasText label;

  protected ObservableObject observableObject;

  protected String path;

  private Function<Object, String> converter;

  public VaadinHasTextBinder(HasText label, ObservableObject observableObject, String path,
      Function<Object, String> converter) {
    super();
    this.label = label;
    this.observableObject = observableObject;
    this.path = path;
    this.converter = converter;

    subscribeToUIEvent();
  }

  protected void subscribeToUIEvent() {
    observableObject.onPropertyChange(PathUtility.getParentPath(path),
        PathUtility.getLastPath(path), value -> onUIStateChanged(value));
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
