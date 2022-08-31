package org.smartbit4all.sec.authentication;

import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class LocalAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private LocalAuthenticationService authenticationService;

  @Autowired
  private SessionApi sessionApi;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String name = authentication.getName();
    String password = authentication.getCredentials().toString();

    try {
      authenticationService.login(name, password);
    } catch (Exception e) {
      throw new BadCredentialsException("Login has failed", e);
    }
    Session session = sessionApi.currentSession();
    return new LocalAuthTokenProvider().getToken(session);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

}
