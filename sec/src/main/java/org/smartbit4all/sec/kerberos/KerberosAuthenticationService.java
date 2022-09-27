package org.smartbit4all.sec.kerberos;

public interface KerberosAuthenticationService {

  public static final String KIND = "kerberosAuthentication";

  void login(String username, String password) throws Exception;

  void logout();

}
