package org.smartbit4all.sec.session;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.session.exception.NoCurrentSessionException;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.application.TimeManagementService;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionApiImpl implements SessionApi {

  private static final Logger log = LoggerFactory.getLogger(SessionApiImpl.class);

  private static final String ERR_NULLKEY = "key cannot be null";
  private static final String ERR_NULLVALUE = "value cannot be null";

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TimeManagementService timeService;

  @Override
  public User getUser() {
    Session session = currentSession();
    return session.getUser() == null ? null : orgApi.getUser(session.getUser());
  }

  @Override
  public URI getUserUri() {
    return currentSession().getUser();
  }

  @Override
  public URI getSessionUri() {
    return currentSession().getUri();
  }

  @Override
  public OffsetDateTime getExpiration() {
    return currentSession().getExpiration();
  }

  @Override
  public Locale getLocale() {
    String locale = null;
    try {
      locale = currentSession().getLocale();
    } catch (Exception e) {
      // NOP
    }
    return locale != null ? Locale.forLanguageTag(locale) : null;
  }

  @Override
  public List<AccountInfo> getAuthentications() {
    return currentSession().getAuthentications();
  }

  @Override
  public AccountInfo getAuthentication(String kind) {
    Assert.notNull(kind, "kind cannot be null");
    return getAuthentications().stream()
        .filter(ai -> kind.equals(ai.getKind()))
        .findFirst()
        .orElse(null);
  }

  @Override
  public String getParameter(String key) {
    Assert.notNull(key, ERR_NULLKEY);
    return currentSession().getParameters().get(key);
  }

  @Override
  public void setParameter(String key, String value) {
    Assert.notNull(key, ERR_NULLKEY);
    Assert.notNull(value, ERR_NULLVALUE);
    sessionManagementApi.setSessionParameter(getSessionUri(), key, value);
  }

  @Override
  public String removeParameter(String key) {
    Assert.notNull(key, ERR_NULLKEY);
    return sessionManagementApi.removeSessionParameter(getSessionUri(), key);
  }

  @Override
  public <T> T getParameterObject(String key, Class<T> clazz) {
    return sessionManagementApi.getSessionParameterObject(getSessionUri(), key, clazz);
  }

  @Override
  public <T> void setParameterObject(String key, T value) {
    sessionManagementApi.setSessionParameterObject(getSessionUri(), key, value);
  }

  @Override
  public Map<String, URI> getViewContexts() {
    return currentSession().getViewContexts();
  }

  @Override
  public void addViewContext(UUID viewContextUuid, URI viewContextUri) {
    sessionManagementApi.addViewContext(getSessionUri(), viewContextUuid, viewContextUri);
  }

  private Session currentSession() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new NoCurrentSessionException("session.security.nocontext",
          "There is no Authentication available in the security context!");
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof SessionAuthPrincipal) {
      URI sessionUri = ((SessionAuthPrincipal) principal).getSessionUri();
      Session session = sessionManagementApi.readSession(sessionUri);
      if (session == null) {
        throw new NoCurrentSessionException("session.invalidsessionuri",
            "The SessionAuthPrincipal holds an invalid session uri!");
      }
      return session;
    }
    throw new NoCurrentSessionException("session.notinitialized",
        "The security context does not contain a Sb4SessionAuthPrincipal - session may not have been initilized!");
  }

  @Override
  public UserActivityLog createActivityLog() {
    User user = getUser();
    UserActivityLog result = new UserActivityLog().userUri(getUserUri());
    if (user != null) {
      String externalUsername =
          getParameter(org.smartbit4all.api.session.Session.EXTRENAL_USERNAME);
      result.userName(user.getUsername()).name(user.getName()
          + (externalUsername != null ? " (" + externalUsername + ")" : StringConstant.EMPTY));
    }
    // TODO manage the zone from the session...
    result.timestamp(OffsetDateTime.now());
    return result;
  }

}
