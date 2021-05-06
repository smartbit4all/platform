package org.smartbit4all.ui.common.filter;

import org.smartbit4all.core.event.EventSubscription;

public class PropertyChangeEventSubscription extends EventSubscription<PropertyChange> {

  private String propertyName;

  public PropertyChangeEventSubscription onPropertyChange(String propertyName) {
    this.propertyName = propertyName;
    return this;
  }

  @Override
  public boolean checkEvent(PropertyChange eventObject) {
    return eventObject != null && eventObject.getProperty().equals(propertyName);
  }

}
