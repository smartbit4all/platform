package org.smartbit4all.api.object;

import org.smartbit4all.domain.meta.EventDefinition;

public interface PropertyChangeEvent extends EventDefinition<PropertyChange<?>> {

  @Override
  PropertyChangeSubscription subscribe();

}
