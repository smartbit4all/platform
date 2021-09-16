package org.smartbit4all.core.object.utility;

import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.core.object.PropertyEntry;
import org.smartbit4all.core.object.PropertyMeta.PropertyKind;

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

    onPropertyChange(observableObject, propertyName);
  }

  private void onPropertyChange(ObservableObject observableObject, String propertyName) {
    ApiObjectRef ref = ((ObservableObjectImpl) observableObject).getRef();
    for (PropertyEntry entry : ref.getProperties()) {
      if (upper(entry.getPath()).equals(upper(propertyName))) {
        PropertyKind kind = entry.getMeta().getKind();
        
        if (kind.equals(PropertyKind.VALUE)) {
          
          observableObject.onPropertyChange(
              null, 
              propertyName, 
              change -> setProperty(change.getNewValue()));
          
        } else if (kind.equals(PropertyKind.REFERENCE)) {
          
          observableObject.onReferencedObjectChange(
              null, 
              propertyName, 
              change -> setProperty(change.getChange().getObject()));
          
        }
      }
    }
  }
  
  private String upper(String text) {
    return text == null ? "" : text.toUpperCase();
  }

  @SuppressWarnings("unchecked")
  private void setProperty(Object object) {
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
