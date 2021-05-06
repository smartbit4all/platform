package org.smartbit4all.domain.meta;

import org.smartbit4all.core.event.EventSubscription;

public class MyEventSubscription extends EventSubscription<String> {

  private String prefix;

  public MyEventSubscription whenStartWith(String prefix) {
    this.prefix = prefix;
    return this;
  }

  @Override
  public boolean checkEvent(String eventObject) {
    return eventObject != null && eventObject.startsWith(prefix);
  }

}
