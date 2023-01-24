package org.smartbit4all.api.invocation;

import org.smartbit4all.core.utility.concurrent.FutureValue;

public class TestEventSubscriberApiImpl implements TestEventSubscriberApi {

  public static FutureValue<String> eventResult = new FutureValue<>();

  @Override
  public void eventConsumer1(String param) {
    eventResult.setValue("event1: " + param);
  }

  @Override
  public void eventConsumer2(String param) {
    eventResult.setValue("event2: " + param);
  }

}
