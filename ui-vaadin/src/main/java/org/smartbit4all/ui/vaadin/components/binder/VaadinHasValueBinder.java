package org.smartbit4all.ui.vaadin.components.binder;

import org.smartbit4all.api.object.ObjectEditing;
import org.smartbit4all.api.object.PropertyChange;
import org.smartbit4all.core.utility.PathUtility;
import com.vaadin.flow.component.HasValue;

public class VaadinHasValueBinder<V> {

  protected ObjectEditing editing;

  protected String path;

  protected boolean propertyChangeProgress = false;

  private HasValue<?, V> field;

  private Class<V> fieldTypeClass;

  public VaadinHasValueBinder(HasValue<?, V> field, ObjectEditing editing, String path,
      Class<V> fieldTypeClass) {
    super();
    this.field = field;
    this.editing = editing;
    this.path = path;
    this.fieldTypeClass = fieldTypeClass;

    subscribeToUIEvent();
  }

  protected void subscribeToUIEvent() {
    editing.publisher().properties().subscribe()
        .property(PathUtility.getParentPath(path), PathUtility.getLastPath(path))
        .add(this::onPropertyChanged);
  }

  private void onPropertyChanged(PropertyChange value) {
    propertyChangeProgress = true;
    onUIStateChanged(value);
    propertyChangeProgress = false;
  }

  protected void onUIStateChanged(PropertyChange value) {
    Object newValue = value.getNewValue();
    if (newValue == null) {
      if (field.isEmpty()) {
        return;
      }
      field.setValue(field.getEmptyValue());
    } else {
      if (fieldTypeClass.isInstance(newValue)) {
        field.setValue((V) newValue);
      } else {
        throw new RuntimeException("Uncompatible types, trying to set "
            + newValue.getClass().getName() + " to " + fieldTypeClass.getName() + " field!");
      }
    }
  }

  protected void setUIState(V value) {
    if (!propertyChangeProgress) {
      editing.setValue(path, value);
    }
  }

  protected void registerTextFieldValueChangeListener() {
    field.addValueChangeListener(event -> setUIState(event.getValue()));
  }
}
