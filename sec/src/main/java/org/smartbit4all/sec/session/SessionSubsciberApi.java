package org.smartbit4all.sec.session;

import java.net.URI;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;

public interface SessionSubsciberApi {

  void sessionCreated(URI sessionURI);

  void sessionModified(Session prevSession, Session nextSession);

  void sessionExpired(Session expiredSession);

  void loginFailed(URI sessionUri, String user, String reason);

  void loginSucceeded(URI sessionUri, User user);

  void logout(URI sessionUri, AccountInfo accountInfo, User user);

}
