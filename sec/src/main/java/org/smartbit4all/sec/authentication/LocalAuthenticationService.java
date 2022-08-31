package org.smartbit4all.sec.authentication;

public interface LocalAuthenticationService {

  public static final String KIND = "localAuthentication";

  void login(String username, String password) throws Exception;

  void logout();

}
