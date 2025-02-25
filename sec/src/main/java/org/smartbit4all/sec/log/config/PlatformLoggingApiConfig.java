package org.smartbit4all.sec.log.config;

import java.util.concurrent.TimeUnit;
import org.smartbit4all.api.invocation.AsyncInvocationChannel;
import org.smartbit4all.api.invocation.AsyncInvocationChannelImpl;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.smartbit4all.sec.log.SessionLoggingApi;
import org.smartbit4all.sec.log.SessionLoggingApiImpl;
import org.springframework.context.annotation.Bean;

public class PlatformLoggingApiConfig {
  public static final String EVENT_SUBSCRIPTION_CHANNEL = "loggingEventSubscriptionChannel";

  @Bean
  public AsyncInvocationChannel sessionSubscriptionChannel() {
    return new AsyncInvocationChannelImpl(EVENT_SUBSCRIPTION_CHANNEL).threadPool(1, 1, 1,
        TimeUnit.MINUTES);
  }

  @Bean
  public SessionLoggingApi sessionLoggingApi() {
    return new SessionLoggingApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<SessionLoggingApi> sessionLoggingApiProvider(
      SessionLoggingApi api) {
    return Invocations.asProvider(SessionLoggingApi.class,
        SessionLoggingApi.class.getName(), api);
  }
}
