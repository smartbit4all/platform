package org.smartbit4all.sec.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
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
import org.smartbit4all.api.session.exception.NoCurrentSessionException;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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
  public String getLocale() {
    return currentSession().getLocale();
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

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getParameterObject(String key, Class<T> clazz) {
    String valueTxt = getParameter(key);
    if (String.class.isAssignableFrom(clazz)) {
      return (T) valueTxt;
    }

    if (ObjectUtils.isEmpty(valueTxt)) {
      return null;
    }
    if (objectMapper.canDeserialize(objectMapper.constructType(clazz))) {
      try {
        return objectMapper.readValue(valueTxt, clazz);
      } catch (JsonProcessingException e) {
        log.warn(
            "Parameter can not be deserialized from session with ObjectMapper. Key: [{}], class: [{}]",
            key,
            clazz.getName(), e);
      }
    }
    if (Serializable.class.isAssignableFrom(clazz)) {
      try {
        return deserializeSerializable(valueTxt);
      } catch (Exception e) {
        log.warn(
            "Parameter can not be deserialized from session as Serializable. Key: [{}], class: [{}]",
            key,
            clazz.getName(), e);
      }
    }
    throw new IllegalArgumentException(
        "The requested parameter can not be deserialized as class " + clazz.getName());
  }

  @Override
  public <T> void setParameterObject(String key, T value) {
    Assert.notNull(key, ERR_NULLKEY);
    Assert.notNull(value, ERR_NULLVALUE);

    Class<? extends Object> clazz = value.getClass();
    if (String.class.isAssignableFrom(clazz)) {
      sessionManagementApi.setSessionParameter(getSessionUri(), key, (String) value);
      return;
    }

    String valueTxt = null;

    if (objectMapper.canSerialize(clazz)) {
      try {
        valueTxt = objectMapper.writeValueAsString(value);
      } catch (JsonProcessingException e) {
        log.warn(
            "Parameter can not be serialized from session with ObjectMapper. Key: [{}], class: [{}]",
            key,
            clazz.getName(), e);
      }
    }

    if (valueTxt == null && Serializable.class.isAssignableFrom(clazz)) {
      try {
        valueTxt = serializeSerializable((Serializable) value);
      } catch (Exception e) {
        log.warn(
            "Parameter can not be deserialized from session as Serializable. Key: [{}], class: [{}]",
            key,
            clazz.getName(), e);
      }
    }
    if (valueTxt != null) {
      sessionManagementApi.setSessionParameter(getSessionUri(), key, valueTxt);
    } else {
      throw new IllegalArgumentException(
          "The the given value of class [" + clazz.getName()
              + "] can not be serilalized and set as session parameter!");
    }
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

  // TODO serilaize and deserialize paramaters should be moved to SessionManagementApiImpl...

  private <T> T deserializeSerializable(String valueTxt) throws Exception {
    byte[] data = Base64.getDecoder().decode(valueTxt);
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
    Object o = ois.readObject();
    ois.close();
    return (T) o;
  }

  private String serializeSerializable(Serializable object) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(object);
    oos.close();
    return Base64.getEncoder().encodeToString(baos.toByteArray());
  }

}
