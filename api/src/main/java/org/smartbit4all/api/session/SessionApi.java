package org.smartbit4all.api.session;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.session.exception.NoCurrentSessionException;

/**
 * The SessionApi interface provides methods to access and manipulate information related to the
 * current user session. It enables retrieving user details, session parameters, and view contexts.
 * An attempt to use this interface without an initialized or accessible session results in a
 * {@link NoCurrentSessionException}.
 */
public interface SessionApi {

  /**
   * Retrieves the current session's user details.
   *
   * @return User object containing details of the user associated with the current session.
   */
  User getUser();

  /**
   * Fetches the URI associated with the current user.
   *
   * @return URI of the current user.
   */
  URI getUserUri();

  /**
   * Obtains the URI of the current session.
   *
   * @return URI representing the current session.
   */
  URI getSessionUri();

  /**
   * Gets the expiration time of the current session.
   *
   * @return OffsetDateTime indicating when the current session expires.
   */
  OffsetDateTime getExpiration();

  /**
   * Retrieves the locale of the current session.
   *
   * @return Locale of the current session.
   */
  Locale getLocale();

  /**
   * Provides a list of account information objects representing the authentications within the
   * session.
   *
   * @return List of AccountInfo objects related to the session's authentications.
   */
  List<AccountInfo> getAuthentications();

  /**
   * Retrieves authentication information by its kind.
   *
   * @param kind The kind of authentication to retrieve.
   * @return AccountInfo object representing the requested authentication kind.
   */
  AccountInfo getAuthentication(String kind);

  /**
   * Retrieves a session parameter value by its key.
   *
   * @param key The key of the parameter to retrieve.
   * @return String value of the parameter, or null if not found.
   */
  String getParameter(String key);

  /**
   * Sets or updates a session parameter.
   *
   * @param key The key of the parameter to set or update.
   * @param value The value to associate with the key.
   */
  void setParameter(String key, String value);

  /**
   * Removes a parameter from the session.
   *
   * @param key The key of the parameter to remove.
   * @return The value of the removed parameter, or null if not found.
   */
  String removeParameter(String key);

  /**
   * Retrieves a session parameter as an object of the specified class.
   *
   * @param key The key of the parameter.
   * @param clazz The class of the object to return.
   * @return An object of type T, or null if not found.
   */
  <T> T getParameterObject(String key, Class<T> clazz);

  <T> T getParameterObject(String key);

  /**
   * Sets or updates a session parameter as an object.
   *
   * @param key The key of the parameter.
   * @param value The object to associate with the key.
   */
  <T> void setParameterObject(String key, T value);

  /**
   * Provides a map of view contexts associated with the session.
   *
   * @return Map with keys as String identifiers and values as URI of the view contexts.
   */
  Map<String, URI> getViewContexts();

  /**
   * Adds a view context to the session.
   *
   * @param viewContextUuid The UUID of the view context.
   * @param viewContextUri The URI of the view context.
   */
  void addViewContext(UUID viewContextUuid, URI viewContextUri);

  /**
   * Creates a new UserActivityLog for logging purposes.
   *
   * @return UserActivityLog instance for the current session's user activity.
   */
  UserActivityLog createActivityLog();

  /**
   * TODO define callback signature callback(UUID viewUuid, String parameterName)
   *
   * @param key
   * @param callback
   */
  void subscribeForParameterChange(String key, UUID viewContextUuid, UUID viewUuid,
      InvocationRequest callback);

  // Constants defining standard HTTP header names for session parameters.
  static final String FULLURL = "HTTP-FULLURL";
  static final String REFERER = "HTTP-REFERER";
  static final String CLIENTIPADDR = "HTTP-CLIENTIPADDR";
  static final String CLIENTOS = "HTTP-CLIENTOS";
  static final String CLIENTBROWSER = "HTTP-CLIENTBROWSER";
  static final String USERAGENT = "HTTP-USERAGENT";
}
