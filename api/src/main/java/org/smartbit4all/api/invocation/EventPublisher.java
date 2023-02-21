package org.smartbit4all.api.invocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.bean.EventSubscriptionData;
import org.smartbit4all.api.invocation.bean.EventSubscriptionType;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.Session;

/**
 * The event publisher is a kind of builder for publishing events.
 * 
 * @author Peter Boros
 */
public class EventPublisher<P, S> {

  private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

  private Class<P> publisherApiClass;

  private Class<S> subscriberApiClass;

  private InvocationApi invocationApi;

  private InvocationRegisterApi invocationRegisterApi;

  private SessionManagementApi sessionManagementApi;

  private String event;

  public EventPublisher(InvocationApi invocationApi, InvocationRegisterApi invocationRegisterApi,
      SessionManagementApi sessionManagementApi,
      Class<P> publisherApiClass, Class<S> subscriberApiClass, String event) {
    super();
    this.publisherApiClass = publisherApiClass;
    this.subscriberApiClass = subscriberApiClass;
    this.invocationApi = invocationApi;
    this.invocationRegisterApi = invocationRegisterApi;
    this.sessionManagementApi = sessionManagementApi;
    this.event = event;
  }

  public void publish(Consumer<S> apiCall) {
    ApiDescriptor api = invocationRegisterApi.getApi(publisherApiClass.getName(), null);
    if (api == null) {
      throw new IllegalArgumentException(
          "The publisher api " + publisherApiClass + " was not found.");
    }

    Map<String, List<InvocationRequest>> requestsByChannel = new HashMap<>();
    for (EventSubscriptionData sub : api.getApiData().getEventSubscriptions()) {
      if (event.equals(sub.getEvent())) {
        List<InvocationRequest> channelRequests =
            requestsByChannel.computeIfAbsent(sub.getChannel(), c -> new ArrayList<>());
        if (sub.getType() == EventSubscriptionType.ONE_RUNTIME) {
          channelRequests
              .add(invocationApi.builder(subscriberApiClass).build(apiCall)
                  .interfaceClass(sub.getSubscribedApi()).methodName(sub.getSubscribedMethod()));
        } else if (sub.getType() == EventSubscriptionType.ALL_RUNTIMES) {
          // TODO implement broadcast
        } else if (sub.getType() == EventSubscriptionType.SESSIONS) {
          if (sessionManagementApi != null) {
            List<Session> activeSessions = sessionManagementApi.getActiveSessions(sub.getEvent());
            activeSessions.stream()
                .map(s -> invocationApi.builder(subscriberApiClass).build(apiCall)
                    .interfaceClass(sub.getSubscribedApi()).methodName(sub.getSubscribedMethod())
                    .sessionUri(s.getUri()))
                .forEach(r -> channelRequests.add(r));
          } else {
            log.warn("Unable to publish for session. {}", sub);
          }
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
