package org.smartbit4all.api.invocation;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;

public class TestEventPublisherApiImpl implements TestEventPublisherApi {

  @Autowired
  private InvocationApi invocationApi;

  private Random rnd = new Random();

  @Override
  public void event(String param) {}

  @Override
  public String fireSomeEvent(String param) {
    String event = rnd.nextBoolean() ? EVENT1 : EVENT2;
    invocationApi.publisher(TestEventPublisherApi.class, event).publish(api -> api.event(param));
    return event;
  }

}
