package org.smartbit4all.domain.meta;

import org.smartbit4all.core.event.EventPublisher;

public interface MyEventPublisher extends EventPublisher {

  EventDefinitionString stringEvent();

  EventDefinitionString stringEvent2();

  MyEventDefinition myEvent();

}
