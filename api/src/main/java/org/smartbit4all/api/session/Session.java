package org.smartbit4all.api.session;

import java.util.HashMap;
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

}

