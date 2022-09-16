package org.smartbit4all.sec.localauth;

import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Authenticates username-password credentials and creates a LocalAuthentication token.
 */
public class LocalAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private LocalAuthenticationService authenticationService;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String name = authentication.getName();
    String password = authentication.getCredentials().toString();

    try {
      authenticationService.login(name, password);
    } catch (Exception e) {
      throw new BadCredentialsException("Login has failed", e);
    }
    Session session = sessionManagementApi.readSession(sessionApi.getSessionUri());
    return new LocalAuthTokenProvider().getToken(session);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

}
