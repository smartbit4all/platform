package org.smartbit4all.ui.vaadin.components.binder;

import org.smartbit4all.api.object.ObjectEditing;
import org.smartbit4all.api.object.PropertyChange;
import org.smartbit4all.core.utility.PathUtility;
import com.vaadin.flow.component.HasText;

public class VaadinHasTextBinder {

  protected HasText label;

  protected ObjectEditing editing;

  protected String path;

  public VaadinHasTextBinder(HasText label, ObjectEditing editing, String path) {
    super();
    this.label = label;
    this.editing = editing;
    this.path = path;

    subscribeToUIEvent();
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
      label.setText(newValue.toString());
    }

  }

}
