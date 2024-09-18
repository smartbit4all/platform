package org.smartbit4all.sec.session;

import java.io.Serializable;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.utils.SessionUtils;
import org.springframework.session.MapSession;

public class SpringSessionWrapper implements org.springframework.session.Session {

  private final URI sessionUri;
  private final SessionManagementApi sessionManagementApi;
  // FIXME to delegate non serialized session function we use MapSession
  // maybe it would be better to save these fields as parameters, so these get saved
  private final MapSession mapSession = new MapSession();

  private SpringSessionWrapper(URI sessionUri, SessionManagementApi sessionManagementApi) {
    Objects.requireNonNull(sessionUri, "sessionUri can not be null!");
    Objects.requireNonNull(sessionManagementApi, "sessionManagementApi can not be null!");

    this.sessionUri = sessionUri;
    this.sessionManagementApi = sessionManagementApi;
  }

  public static SpringSessionWrapper of(URI sessionUri,
      SessionManagementApi sessionManagementApi) {
    return new SpringSessionWrapper(sessionUri, sessionManagementApi);
  }

  public URI getSessionUri() {
    return sessionUri;
  }

  @Override
  public String getId() {
    return sessionUri.toString();
  }

  @Override
  public String changeSessionId() {
    // nope
    return null;
  }

  @Override
  public <T> T getAttribute(String attributeName) {
    Session session = readSession();
    return (T) SessionUtils.deserializeSessionParameter(session.getParameters().get(attributeName),
        Serializable.class, null);
  }

  private Session readSession() {
    sessionManagementApi.initCurrentSession(sessionUri);
    Session session = sessionManagementApi.readSession(sessionUri);
    return session;
  }

  @Override
  public Set<String> getAttributeNames() {
    return readSession().getParameters().keySet();
  }

  @Override
  public void setAttribute(String attributeName, Object attributeValue) {
    String serializedValue = SessionUtils.serializeSessionParameter(attributeValue, null);
    sessionManagementApi.setSessionParameterObject(sessionUri, attributeName, serializedValue);
  }

  @Override
  public void removeAttribute(String attributeName) {
    sessionManagementApi.removeSessionParameter(sessionUri, attributeName);
  }

  @Override
  public Instant getCreationTime() {
    return readSession().getCreatedAt().toInstant();
  }

  @Override
  public void setLastAccessedTime(Instant lastAccessedTime) {
    mapSession.setLastAccessedTime(lastAccessedTime);
  }

  @Override
  public Instant getLastAccessedTime() {
    return mapSession.getLastAccessedTime();
  }

  @Override
  public void setMaxInactiveInterval(Duration interval) {
    mapSession.setMaxInactiveInterval(interval);
  }

  @Override
  public Duration getMaxInactiveInterval() {
    return mapSession.getMaxInactiveInterval();
  }

  @Override
  public boolean isExpired() {
    return readSession().getExpiration().isBefore(OffsetDateTime.now());
  }

}
