package org.smartbit4all.core.object.utility;

import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PropertyChange;

/**
 * Track the property value of an ObservableObject property.
 * 
 * @author Zoltan Szegedi
 * 
 * @param <T>
 */
public class ObjectPropertyChangeTracker<T> {

  private T property;

  public ObjectPropertyChangeTracker(ObservableObject observableObject, String propertyName) {
    property = null;
    
    observableObject.onPropertyChange(
        null,
        propertyName,
        change -> onPropertyChange(change));
  }

  @SuppressWarnings("unchecked")
  private void onPropertyChange(PropertyChange propertyChange) {
    Object object = propertyChange.getNewValue();
    if (object == null) {
      property = null;
    } else {
      property = (T) object;
    }
  }

  public T getValue() {
    return property;
  }

}
