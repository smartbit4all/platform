package org.smartbit4all.api.session;

import java.net.URI;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.domain.data.storage.StorageObject;

public interface UserSessionApi {

  static final String SCHEME = "usersession";

  User currentUser();

  default StorageObject<Session> startSession(URI userUri) {
    throw new UnsupportedOperationException(
        "UserSessionApi session hadling not implemented in " + this.getClass().getName() + "!");
  }

  default void saveSession(StorageObject<Session> session) {
    throw new UnsupportedOperationException(
        "UserSessionApi session hadling not implemented in " + this.getClass().getName() + "!");
  }

  default StorageObject<Session> currentSession() {
    throw new UnsupportedOperationException(
        "UserSessionApi session hadling not implemented in " + this.getClass().getName() + "!");
  }

}
