package org.smartbit4all.api.session;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
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

  void setSessionExpiration(URI sessionUri, OffsetDateTime expiration,
      OffsetDateTime refreshExpiration);

  void addSessionAuthentication(URI sessionUri, AccountInfo accountInfo);

  void removeSessionAuthentication(URI sessionUri, String kind);

  void removeSessionAuthentications(URI sessionUri);

  void setSessionUser(URI sessionUri, URI userUri);

  void addViewContext(URI sessionUri, UUID viewContextUuid, URI viewContextUri);

  void startTechnicalSession(URI technicalUserUri);

  void setSession(URI sessionUri);

  /**
   * Read all the active sessions that are still active it is a relatively expensive function
   * because it clean up the inactive sessions from the active storage registry.
   * 
   * @return
   */
  List<Session> getActiveSessions();

  /**
   * Read all the active sessions that are still active it is a relatively expensive function
   * because it clean up the inactive sessions from the active storage registry.
   * 
   * @param sessionListName The session list where the active sessions come from. If this list
   *        doesn't exist then we get back the {@link #getActiveSessions()} result.
   * @return
   */
  List<Session> getActiveSessions(String sessionListName);

  void addToList(URI sessionUri, String sessionListName);

  void removeFromList(URI sessionUri, String sessionListName);

  List<Session> getActiveSessionsOfUser(URI orgUserUri);

}
