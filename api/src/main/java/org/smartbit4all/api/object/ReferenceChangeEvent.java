package org.smartbit4all.api.object;

import org.smartbit4all.domain.meta.EventDefinition;

public interface ReferenceChangeEvent extends EventDefinition<ReferenceChange> {

  @Override
  ReferenceChangeSubscription subscribe();

}
