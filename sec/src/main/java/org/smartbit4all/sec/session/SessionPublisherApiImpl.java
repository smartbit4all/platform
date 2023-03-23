package org.smartbit4all.sec.session;

import java.net.URI;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.springframework.beans.factory.annotation.Autowired;

public class SessionPublisherApiImpl implements SessionPublisherApi {

  @Autowired
  private InvocationApi invocationApi;

  @Override
  public void fireSessionCreated(URI sessionUri) {
    invocationApi
        .publisher(SessionPublisherApi.class, SessionSubsciberApi.class,
            SessionPublisherApi.SESSION_CREATED)
        .publish(api -> api.sessionCreated(sessionUri));
  }

  @Override
  public void fireSessionModified(Session prevSession, Session nextSession) {
    invocationApi
        .publisher(SessionPublisherApi.class, SessionSubsciberApi.class,
            SessionPublisherApi.SESSION_MODIFIED)
        .publish(api -> api.sessionModified(prevSession, nextSession));
  }

  @Override
  public void fireSessionExpired(Session expiredSession) {
    invocationApi
        .publisher(SessionPublisherApi.class, SessionSubsciberApi.class,
            SessionPublisherApi.SESSION_EXPIRED)
        .publish(api -> api.sessionExpired(expiredSession));
  }

  @Override
  public void fireOnLoginSucceeded(URI sessionUri, User user) {
    invocationApi
        .publisher(SessionPublisherApi.class, SessionSubsciberApi.class,
            SessionPublisherApi.LOGIN_SUCCEEDED)
        .publish(api -> api.loginSucceeded(sessionUri, user));
  }

  @Override
  public void fireOnLoginFailed(URI sessionUri, String user, String reason) {
    invocationApi
        .publisher(SessionPublisherApi.class, SessionSubsciberApi.class,
            SessionPublisherApi.LOGIN_FAILED)
        .publish(api -> api.loginFailed(sessionUri, user, reason));
  }

  @Override
  public void fireOnLogout(URI sessionUri, AccountInfo accountInfo, User user) {
    invocationApi
        .publisher(SessionPublisherApi.class, SessionSubsciberApi.class,
            SessionPublisherApi.LOGOUT)
        .publish(api -> api.logout(sessionUri, accountInfo, user));
  }
}
