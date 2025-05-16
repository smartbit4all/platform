package org.smartbit4all.sec.localauth;

import org.smartbit4all.api.authentication.AuthenticationService;

public interface LocalAuthenticationService extends AuthenticationService {

  public static final String KIND = "localAuthentication";

  void login(String username, String password) throws Exception;

  void logout();

}
