package org.smartbit4all.ui.common.filter;

import org.smartbit4all.domain.meta.EventDefinition;

public interface PropertyChangeEvent extends EventDefinition<PropertyChange> {

  @Override
  PropertyChangeEventSubscription subscribe();

}