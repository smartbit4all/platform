package org.smartbit4all.api.session;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.session.bean.SessionInfoData;

public interface SessionManagementApi {

  static final String SCHEMA = "session-sv";

  /**
   * Calculates the duration between two {@link OffsetDateTime}s in seconds.
   *
   * @param now the start {@link OffsetDateTime} of the period
   * @param refreshExpiration the end {@link OffsetDateTime} of the period
   * @return the number of whole seconds between the two moments in time; 0 is returned if any of
   *         the input parameters are null; {@link Long#MAX_VALUE} is returned if the calculation
   *         would result in a long overflow
   */
  static long getRefreshTokenLifetime(OffsetDateTime now, OffsetDateTime refreshExpiration) {
    if (now == null || refreshExpiration == null) {
      return 0L;
    }

    try {
      return ChronoUnit.SECONDS.between(now, refreshExpiration);
    } catch (Exception e) {
      // let's be extra safe ( the above throws if it would cause an overflow -> we can just default
      // to a very large number ):
      return Long.MAX_VALUE;
    }
  }

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

  <T> void setSessionParameterObject(URI sessionUri, String key, T value);

  <T> T getSessionParameterObject(URI sessionUri, String key, Class<T> clazz);

  <T> T getSessionParameterObject(URI sessionUri, String key);

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

  void startTechnicalSession();

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

  /**
   * Sets the duration of the session token.
   *
   * @param minutes {@code int} token lifetime in minutes
   */
  void setSessionExpirationTime(final int minutes);

  /**
   * Sets the duration of the session refresh token.
   *
   * @param minutes {@code int} token lifetime in minutes, negative values mean the session is
   *        always refreshable
   */
  void setRefreshTokenExpirationTime(final int minutes);

  URI updateSession(URI sessionUri, UnaryOperator<Session> update);

}
