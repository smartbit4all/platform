package org.smartbit4all.domain.meta;

public interface MyEventDefinition extends EventDefinition<String> {

  @Override
  MyEventSubscription subscribe();

}
