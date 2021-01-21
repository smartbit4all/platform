package org.smartbit4all.api.object;

import org.smartbit4all.domain.meta.EventDefinition;

public interface CollectionChangeEvent extends EventDefinition<CollectionChange> {

  @Override
  CollectionChangeSubscription subscribe();

}
