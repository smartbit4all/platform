package org.smartbit4all.api.kerberosauthentication.restserver;

import org.smartbit4all.api.kerberosauthentication.bean.KerberosAuthenticationLoginRequest;
import org.smartbit4all.sec.kerberos.KerberosAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.ObjectUtils;

public class KerberosAuthenticationApiDelegateImpl implements KerberosAuthenticationApiDelegate {

  @Autowired
  private KerberosAuthenticationService kerberosAuthentication;

  @Override
  public ResponseEntity<Void> login(KerberosAuthenticationLoginRequest request) throws Exception {


    String username = request.getUsername();
    String password = request.getPassword();

    if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    try {
      kerberosAuthentication.login(username, password);
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> logout() throws Exception {
    kerberosAuthentication.logout();
    return ResponseEntity.ok().build();
  }

}
