package org.smartbit4all.domain.meta;

import org.smartbit4all.core.event.EventDefinition;

public interface MyEventDefinition extends EventDefinition<String> {

  @Override
  MyEventSubscription subscribe();

}
