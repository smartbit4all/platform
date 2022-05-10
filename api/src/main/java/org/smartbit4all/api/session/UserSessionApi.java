package org.smartbit4all.api.session;

import org.smartbit4all.api.org.bean.User;

public interface UserSessionApi {

  static final String SCHEME = "usersession";

  User currentUser();

  default Session startSession(User user) {
    // TODO quick fix
    return null;
    // throw new UnsupportedOperationException(
    // "UserSessionApi session hadling not implemented in " + this.getClass().getName() + "!");
  }

  default Session startSession(User user, String token) {
    return startSession(user);
  }

  default Session currentSession() {
    // TODO quick fix
    return null;
    // throw new UnsupportedOperationException(
    // "UserSessionApi session hadling not implemented in " + this.getClass().getName() + "!");
  }

  default void storeCurrentSession(String token) {
    // NOP
  }

  default Session getSessionByToken(String token) {
    // NOP
    return null;
  }

  default void removeCurrentSession() {
    // NOP
  }
}
