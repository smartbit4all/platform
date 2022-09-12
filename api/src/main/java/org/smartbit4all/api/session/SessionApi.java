package org.smartbit4all.api.session;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.session.bean.SessionInfoData;

public interface SessionApi {

  static final String SCHEMA = "usersession";

  /**
   * Starts a session creating a unique sid.
   */
  SessionInfoData startSession();

  User currentUser();

  Session currentSession();

  Session readSession(URI sessionUri);

  void setSessionParameter(URI sessionUri, String key, String value);

  void removeSessionParameter(URI sessionUri, String key);

  void setSessionLocale(URI sessionUri, String locale);

  void setSessionExpiration(URI sessionUri, OffsetDateTime expiration);

  void addSessionAuthentication(URI sessionUri, AccountInfo accountInfo);

  void removeSessionAuthentication(URI sessionUri, String kind);

  void setSessionUser(URI sessionUri, URI userUri);

  // this is very preliminary
  void addViewContext(UUID viewContextUuid, URI viewContextUri);

}
