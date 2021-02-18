package org.smartbit4all.ui.vaadin.components.binder;

import org.smartbit4all.api.object.ObjectEditing;
import org.smartbit4all.api.object.PropertyChange;
import org.smartbit4all.core.utility.PathUtility;
import com.vaadin.flow.component.html.Label;

public class VaadinLabelBinder {

  protected Label label;

  protected ObjectEditing editing;

  protected String path;

  public VaadinLabelBinder(Label label, ObjectEditing editing, String path) {
    super();
    this.label = label;
    this.editing = editing;
    this.path = path;

    subscribeToUIEvent();
  }

  protected void subscribeToUIEvent() {
    editing.publisher().properties().subscribe().property(PathUtility.getParentPath(path), PathUtility.getLastPath(path)).add(value -> onUIStateChanged(value));
  }

  protected void onUIStateChanged(PropertyChange value) {
    String currentValue = label.getText();
    String newValue = (String) value.getNewValue();

    if (currentValue == null && newValue == null) {
      return;
    }
    
    if ((currentValue == null && newValue != null) || !currentValue.equals(newValue)) {
      label.setText(newValue);
    }
  }

}
