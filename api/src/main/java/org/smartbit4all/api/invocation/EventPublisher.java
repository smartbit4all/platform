package org.smartbit4all.api.invocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.smartbit4all.api.invocation.bean.EventSubscriptionData;
import org.smartbit4all.api.invocation.bean.EventSubscriptionType;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

/**
 * The event publisher is a kind of builder for publishing events from {@link EventPublisherApi}s.
 * 
 * @author Peter Boros
 */
public class EventPublisher<T> {

  private Class<T> apiClass;

  private InvocationApi invocationApi;

  private InvocationRegisterApi invocationRegisterApi;

  private String event;

  public EventPublisher(InvocationApi invocationApi, InvocationRegisterApi invocationRegisterApi,
      Class<T> apiClass, String event) {
    super();
    this.apiClass = apiClass;
    this.invocationApi = invocationApi;
    this.invocationRegisterApi = invocationRegisterApi;
    this.event = event;
  }

  public void publish(Consumer<T> apiCall) {
    ApiDescriptor api = invocationRegisterApi.getApi(apiClass.getName(), null);
    if (api == null) {
      throw new IllegalArgumentException("The publisher api " + apiClass + " was not found.");
    }
    Map<String, List<InvocationRequest>> requestsByChannel = new HashMap<>();
    for (EventSubscriptionData sub : api.getApiData().getEventSubscriptions()) {
      if (event.equals(sub.getEvent())) {
        if (sub.getType() == EventSubscriptionType.ONE_RUNTIME) {
          requestsByChannel.computeIfAbsent(sub.getChannel(), c -> new ArrayList<>())
              .add(invocationApi.builder(apiClass).build(apiCall)
                  .interfaceClass(sub.getSubscribedApi()).methodName(sub.getSubscribedMethod()));
        } else if (sub.getType() == EventSubscriptionType.ALL_RUNTIMES) {
          // TODO implement broadcast
        } else if (sub.getType() == EventSubscriptionType.SESSIONS) {
          // TODO Invocation every related session.
        }
      }
    }
    // Now execute all the requests.
    for (Entry<String, List<InvocationRequest>> entry : requestsByChannel.entrySet()) {
      for (InvocationRequest request : entry.getValue()) {
        invocationApi.invokeAsync(request, entry.getKey());
      }
    }
  }

}
