package org.smartbit4all.api.localauthentication.restserver.impl;

import org.smartbit4all.api.localauthentication.bean.LocalAuthenticationLoginRequest;
import org.smartbit4all.api.localauthentication.restserver.LocalAuthenticationApiDelegate;
import org.smartbit4all.sec.authentication.LocalAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class LocalAuthenticationApiDelegateImpl implements LocalAuthenticationApiDelegate {

  @Autowired
  private LocalAuthenticationService localAuthentication;

  @Override
  public ResponseEntity<Void> login(LocalAuthenticationLoginRequest request) throws Exception {


    String username = request.getUsername();
    String password = request.getPassword();
    localAuthentication.login(username, password);

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> logout() throws Exception {
    localAuthentication.logout();
    return ResponseEntity.ok().build();
  }

}
