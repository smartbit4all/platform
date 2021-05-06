package org.smartbit4all.ui.common.filter;

import org.smartbit4all.core.event.EventPublisher;

public interface UIStateEvent extends EventPublisher {

  PropertyChangeEvent propertyChangeEvent();
  
}
