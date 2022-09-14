package org.smartbit4all.api.localauthentication.restserver.impl;

import org.smartbit4all.api.localauthentication.bean.LocalAuthenticationLoginRequest;
import org.smartbit4all.api.localauthentication.restserver.LocalAuthenticationApiDelegate;
import org.smartbit4all.sec.localauth.LocalAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.ObjectUtils;

public class LocalAuthenticationApiDelegateImpl implements LocalAuthenticationApiDelegate {

  @Autowired
  private LocalAuthenticationService localAuthentication;

  @Override
  public ResponseEntity<Void> login(LocalAuthenticationLoginRequest request) throws Exception {


    String username = request.getUsername();
    String password = request.getPassword();

    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    try {
      localAuthentication.login(username, password);
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> logout() throws Exception {
    localAuthentication.logout();
    return ResponseEntity.ok().build();
  }

}
