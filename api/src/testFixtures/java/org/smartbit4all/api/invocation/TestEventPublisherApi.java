package org.smartbit4all.api.invocation;

public interface TestEventPublisherApi {

  /**
   * We need this to refer in the {@link EventSubscription} annotation.
   */
  static final String API = "org.smartbit4all.api.invocation.TestEventPublisherApi";

  /**
   * The definition of the event.
   */
  static final String EVENT1 = "event1";

  static final String EVENT2 = "event2";

  /**
   * The api call that will publish the event....
   * 
   * @param param
   * @return
   */
  String fireSomeEvent(String param);

  /**
   * The venet call itself. Now it is not nice because it could be an inner knowledge.
   * 
   * @param param
   */
  void event(String param);

}
