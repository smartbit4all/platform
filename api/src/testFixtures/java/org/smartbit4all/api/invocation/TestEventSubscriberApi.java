package org.smartbit4all.api.invocation;

public interface TestEventSubscriberApi {

  @EventSubscription(api = TestEventPublisherApi.API, event = TestEventPublisherApi.EVENT1,
      channel = InvocationTestConfig.GLOBAL_ASYNC_CHANNEL)
  void eventConsumer1(String param);

  @EventSubscription(api = TestEventPublisherApi.API, event = TestEventPublisherApi.EVENT2,
      channel = InvocationTestConfig.SECOND_ASYNC_CHANNEL)
  void eventConsumer2(String param);

}
