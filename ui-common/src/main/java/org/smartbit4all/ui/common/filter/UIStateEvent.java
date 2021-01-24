package org.smartbit4all.ui.common.filter;

import org.smartbit4all.domain.meta.EventPublisher;

public interface UIStateEvent extends EventPublisher {

  PropertyChangeEvent propertyChangeEvent();
  
}
