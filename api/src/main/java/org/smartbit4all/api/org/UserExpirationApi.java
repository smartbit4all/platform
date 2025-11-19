package org.smartbit4all.api.org;

import java.net.URI;

public interface UserExpirationApi {

  String USER_EXPIRATION_INVOCATION_CHANEL = "userExpirationChanel";

  String USER_EXPIRATION_MAP = "userExpiration";

  void setUserExpiration(URI userUri, Long minutes);

  void disableExpiredUser(URI userUri);

}
