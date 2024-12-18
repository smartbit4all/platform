package org.smartbit4all.sec.session;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.Session;
import org.springframework.session.MapSession;

public class SpringSessionWrapper implements org.springframework.session.Session {

  private static final Logger log = LoggerFactory.getLogger(SpringSessionWrapper.class);

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
    readSession();
    T result = (T) sessionManagementApi.getSessionParameterObject(sessionUri, attributeName);
    log.debug("Getting {} = {}, class: {}", attributeName, result,
        result == null ? null : result.getClass().getName());
    return result;
  }

  private Session readSession() {
    sessionManagementApi.initCurrentSession(sessionUri);
    return sessionManagementApi.readSession(sessionUri);
  }

  @Override
  public Set<String> getAttributeNames() {
    return readSession().getParameters().keySet();
  }

  @Override
  public void setAttribute(String attributeName, Object attributeValue) {
    readSession();
    log.debug("Setting {} = {}, class: {}", attributeName, attributeValue,
        attributeValue == null ? null : attributeValue.getClass().getName());
    sessionManagementApi.setSessionParameterObject(sessionUri, attributeName, attributeValue);
  }

  @Override
  public void removeAttribute(String attributeName) {
    readSession();
    log.debug("Removing {}", attributeName);
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
