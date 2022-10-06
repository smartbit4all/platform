package org.smartbit4all.api.session;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.exception.NoCurrentSessionException;

/**
 *
 * This interface provides methods to access information from the current session. <br/>
 * In case there is no session initialized or it is not reachable {@link NoCurrentSessionException}
 * is thrown!
 * 
 */
public interface SessionApi {

  User getUser();

  URI getUserUri();

  URI getSessionUri();

  OffsetDateTime getExpiration();

  String getLocale();

  List<AccountInfo> getAuthentications();

  AccountInfo getAuthentication(String kind);

  String getParameter(String key);

  void setParameter(String key, String value);

  String removeParameter(String key);

  <T> T getParameterObject(String key, Class<T> clazz);

  <T> void setParameterObject(String key, T value);

  Map<String, URI> getViewContexts();

  // this is very preliminary
  void addViewContext(UUID viewContextUuid, URI viewContextUri);

}
