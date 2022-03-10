package org.smartbit4all.api.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.core.reactive.ObjectChangePublisher;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Session
 */
public class Session {

  public static final String CURRENT_LOCALE = "Session.currentLocale";
  public static final String AVAILABLE_LOCALES = "Session.availableLocales";

  private final UUID uuid;

  private User user;

  private final Map<String, Object> parameters = new HashMap<>();

  private ObjectChangePublisher<String> publisher;

  public Session(ObjectChangePublisher<String> publisher) {
    this.publisher = publisher;
    uuid = UUID.randomUUID();
  }

  public Disposable subscribeForParameterChange(String key, Consumer<String> observer) {
    return publisher.subscribeForChange(key, observer);
  }

  public UUID getUuid() {
    return uuid;
  }

  public Session user(User user) {
    this.user = user;
    return this;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Session parameter(String key, Object value) {
    parameters.put(key, value);
    publisher.onNext(key);
    return this;
  }

  public void setParameter(String key, Object value) {
    parameters.put(key, value);
    publisher.onNext(key);
  }

  public Object getParameter(String key) {
    return parameters.get(key);
  }

  public void clearParameter(String key) {
    parameters.remove(key);
    publisher.onNext(key);
  }

  public Map<String, Object> getParameters() {
    return parameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Session session = (Session) o;
    return Objects.equals(this.uuid, session.uuid) &&
        Objects.equals(this.user, session.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, user);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Session {\n");
    sb.append("    uri: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    userUri: ").append(toIndentedString(user)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  /**
   * Puts a key/value pair into a map in this session, specified by parameterName. If map doesn't
   * exists yet, it will be created (new HashMap).
   * 
   * @param <K>
   * @param <V>
   * @param key
   * @param value
   * @param parameterName
   */
  public <K, V> void putValueToMap(K key, V value, String parameterName) {
    @SuppressWarnings("unchecked")
    Map<K, V> map = (Map<K, V>) getParameter(parameterName);
    if (map == null) {
      map = new HashMap<>();
      setParameter(parameterName, map);
    }
    map.put(key, value);
  }

  /**
   * Returns value from a map specified by parameterName. If map is not present, returns null.
   * 
   * @param <K>
   * @param <V>
   * @param key
   * @param parameterName
   * @return
   */
  public <K, V> V getValueFromMap(K key, String parameterName) {
    @SuppressWarnings("unchecked")
    Map<K, V> map = (Map<K, V>) getParameter(parameterName);
    if (map != null) {
      return map.get(key);
    }
    return null;
  }

  /**
   * Clears and removes a map from this session specified by parameterName. If map doesn't exists,
   * it does nothing.
   * 
   * @param parameterName
   */
  public void clearMap(String parameterName) {
    Map<?, ?> map = (Map<?, ?>) getParameter(parameterName);
    if (map != null) {
      map.clear();
      clearParameter(parameterName);
    }
  }

  public void setCurrentLocale(String locale) {
    setParameter(CURRENT_LOCALE, locale);
  }

  public String getCurrentLocale() {
    return (String) getParameter(CURRENT_LOCALE);
  }

  public void setAvailableLocales(List<String> locale) {
    setParameter(AVAILABLE_LOCALES, locale);
  }

  @SuppressWarnings("unchecked")
  public List<String> getAvailableLocales() {
    return (List<String>) getParameter(AVAILABLE_LOCALES);
  }

}

