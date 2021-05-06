package org.smartbit4all.ui.common.filter;

import org.smartbit4all.core.event.EventDefinition;

public interface PropertyChangeEvent extends EventDefinition<PropertyChange> {

  @Override
  PropertyChangeEventSubscription subscribe();

}