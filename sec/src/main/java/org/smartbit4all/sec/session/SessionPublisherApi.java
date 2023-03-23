package org.smartbit4all.sec.session;

import java.net.URI;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;

public interface SessionPublisherApi {

  static final String API = "org.smartbit4all.sec.session.SessionPublisherApi";

  static final String SESSION_CREATED = "session_created";

  static final String SESSION_MODIFIED = "session_modified";

  static final String SESSION_EXPIRED = "session_expired";

  static final String LOGIN_SUCCEEDED = "login_succeeded";

  static final String LOGIN_FAILED = "login_failed";

  static final String LOGOUT = "logout";

  void fireSessionCreated(URI sessionUri);

  void fireSessionModified(Session prevSession, Session nextSession);

  void fireSessionExpired(Session expiredSession);

  void fireOnLoginFailed(URI sessionUri, String user, String reason);

  void fireOnLoginSucceeded(URI sessionUri, User user);

  void fireOnLogout(URI sessionUri, AccountInfo accountInfo, User user);

}
