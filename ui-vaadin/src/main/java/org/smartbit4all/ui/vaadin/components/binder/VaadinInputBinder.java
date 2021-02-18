package org.smartbit4all.ui.vaadin.components.binder;

import org.smartbit4all.api.object.ObjectEditing;
import org.smartbit4all.api.object.PropertyChange;
import org.smartbit4all.core.utility.PathUtility;

public abstract class VaadinInputBinder {

  protected ObjectEditing editing;
  
  protected String path;
  
  protected boolean propertyChangeProgress = false;;
  
  public VaadinInputBinder(ObjectEditing editing, String path) {
    super();
    this.editing = editing;
    this.path = path;
    
    subscribeToUIEvent();
  }

  protected void subscribeToUIEvent() {
    editing.publisher().properties().subscribe().property(PathUtility.getParentPath(path), PathUtility.getLastPath(path)).add(value -> onPropertyChanged(value));
  }
  
  private Object onPropertyChanged(PropertyChange value) {
    propertyChangeProgress = true;
    onUIStateChanged(value);
    propertyChangeProgress = false;
    return null;
  }

  protected abstract void onUIStateChanged(PropertyChange value);

  protected void setUIState(Object value) {
    if (!propertyChangeProgress) {
      editing.setValue(path, value);
    }
  }
}
