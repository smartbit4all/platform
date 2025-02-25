package org.smartbit4all.sec.log;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;

public class SessionLoggingApiImpl implements SessionLoggingApi {
  private static final Logger log = LoggerFactory.getLogger(SessionLoggingApiImpl.class);

  @Override
  public void sessionCreatedEvent(URI sessionUri) {
    log.info("Session created");

  }

  @Override
  public void sessionModifiedEvent(Session prevSession, Session nextSession) {
    log.info("Session modified");

  }

  @Override
  public void sessionExpiredEvent(Session expiredSession) {
    log.info("Session is expired");

  }

  @Override
  public void loginSucceededEvent(URI sessionUri, User user) {
    log.info("Login secceeded with user: [{}]", user.getUsername());

  }

  @Override
  public void loginFailedEvent(URI sessionUri, String user, String reason) {
    log.info("Login Faild with user: [{}] and reason: [{}]", user, reason);

  }

  @Override
  public void logoutEvent(URI sessionUri, AccountInfo accountInfo, User user) {
    log.info("Logout with user: [{}]", user.getUsername());

  }



}
