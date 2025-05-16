package org.smartbit4all.sec.kerberos;

import org.smartbit4all.api.authentication.AuthenticationService;

public interface KerberosAuthenticationService extends AuthenticationService {

  public static final String KIND = "kerberosAuthentication";

  @Override
  void login(String username, String password) throws Exception;

  @Override
  void logout();

}
