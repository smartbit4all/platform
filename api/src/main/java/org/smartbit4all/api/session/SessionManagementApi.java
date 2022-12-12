package org.smartbit4all.api.session;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.session.bean.SessionInfoData;

public interface SessionManagementApi {

  static final String SCHEMA = "session-sv";

  /**
   * Starts a session creating a unique sid.
   */
  SessionInfoData startSession();

  /**
   * Refreshes the session by the given refreshToken
   */
  SessionInfoData refreshSession(String refreshToken);

  Session readSession(URI sessionUri);

  Session initCurrentSession(URI sessionUri);

  void setSessionParameter(URI sessionUri, String key, String value);

  String removeSessionParameter(URI sessionUri, String key);

  void setSessionLocale(URI sessionUri, String locale);

  void setSessionExpiration(URI sessionUri, OffsetDateTime expiration);

  void addSessionAuthentication(URI sessionUri, AccountInfo accountInfo);

  void removeSessionAuthentication(URI sessionUri, String kind);

  void setSessionUser(URI sessionUri, URI userUri);

  void addViewContext(URI sessionUri, UUID viewContextUuid, URI viewContextUri);

  void startTechnicalSession(URI technicalUserUri);

}
