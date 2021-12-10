package org.smartbit4all.api.session;

import org.smartbit4all.api.org.bean.User;

public interface UserSessionApi {

  static final String SCHEME = "usersession";

  User currentUser();

  default Session startSession(User user) {
    throw new UnsupportedOperationException(
        "UserSessionApi session hadling not implemented in " + this.getClass().getName() + "!");
  }

  default Session currentSession() {
    throw new UnsupportedOperationException(
        "UserSessionApi session hadling not implemented in " + this.getClass().getName() + "!");
  }

}
