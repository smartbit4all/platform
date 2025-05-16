package org.smartbit4all.api.authentication;

public interface AuthenticationService {

  void login(String username, String password) throws Exception;

  void logout();
}
