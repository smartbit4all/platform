package org.smartbit4all.sec.log;

import java.net.URI;
import org.smartbit4all.api.invocation.EventSubscription;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.log.config.PlatformLoggingApiConfig;
import org.smartbit4all.sec.session.SessionPublisherApi;

public interface SessionLoggingApi {

  @EventSubscription(api = SessionPublisherApi.API,
      event = SessionPublisherApi.SESSION_CREATED,
      channel = PlatformLoggingApiConfig.EVENT_SUBSCRIPTION_CHANNEL)
  void sessionCreatedEvent(URI sessionUri);

  @EventSubscription(api = SessionPublisherApi.API,
      event = SessionPublisherApi.SESSION_MODIFIED,
      channel = PlatformLoggingApiConfig.EVENT_SUBSCRIPTION_CHANNEL)
  void sessionModifiedEvent(Session prevSession, Session nextSession);

  @EventSubscription(api = SessionPublisherApi.API,
      event = SessionPublisherApi.SESSION_EXPIRED,
      channel = PlatformLoggingApiConfig.EVENT_SUBSCRIPTION_CHANNEL)
  void sessionExpiredEvent(Session expiredSession);

  @EventSubscription(api = SessionPublisherApi.API,
      event = SessionPublisherApi.LOGIN_SUCCEEDED,
      channel = PlatformLoggingApiConfig.EVENT_SUBSCRIPTION_CHANNEL)
  void loginSucceededEvent(URI sessionUri, User user);

  @EventSubscription(api = SessionPublisherApi.API,
      event = SessionPublisherApi.LOGIN_FAILED,
      channel = PlatformLoggingApiConfig.EVENT_SUBSCRIPTION_CHANNEL)
  void loginFailedEvent(URI sessionUri, String user, String reason);

  @EventSubscription(api = SessionPublisherApi.API,
      event = SessionPublisherApi.LOGOUT,
      channel = PlatformLoggingApiConfig.EVENT_SUBSCRIPTION_CHANNEL)
  void logoutEvent(URI sessionUri, AccountInfo accountInfo, User user);
}
