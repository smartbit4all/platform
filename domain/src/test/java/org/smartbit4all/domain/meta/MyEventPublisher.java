package org.smartbit4all.domain.meta;

public interface MyEventPublisher extends EventPublisher {

  EventDefinitionString stringEvent();

  EventDefinitionString stringEvent2();

  MyEventDefinition myEvent();

}
