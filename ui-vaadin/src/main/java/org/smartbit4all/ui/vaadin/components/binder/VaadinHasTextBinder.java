package org.smartbit4all.ui.vaadin.components.binder;

import java.util.function.Function;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.utility.PathUtility;
import com.vaadin.flow.component.HasText;

public class VaadinHasTextBinder {

  protected HasText label;

  protected ObjectEditing editing;

  protected String path;

  private Function<Object, String> converterFunction;

  public VaadinHasTextBinder(HasText label, ObjectEditing editing, String path) {
    super();
    this.label = label;
    this.editing = editing;
    this.path = path;

    subscribeToUIEvent();
  }
  
  public void setConverterFunction(Function<Object, String> converterFunction) {
    this.converterFunction = converterFunction;
  }

  protected void subscribeToUIEvent() {
    editing.publisher().properties().subscribe()
        .property(PathUtility.getParentPath(path), PathUtility.getLastPath(path))
        .add(value -> onUIStateChanged(value));
  }

  protected void onUIStateChanged(PropertyChange value) {
    String currentValue = label.getText();
    Object newValue = value.getNewValue();

    newValue = newValue == null ? "" : newValue;
    currentValue = currentValue == null ? "" : currentValue;

    if (!currentValue.equals(newValue)) {
      if (converterFunction != null) {
        label.setText(converterFunction.apply(newValue));
      } else {
        label.setText(newValue.toString());
      }
    }

  }

}
