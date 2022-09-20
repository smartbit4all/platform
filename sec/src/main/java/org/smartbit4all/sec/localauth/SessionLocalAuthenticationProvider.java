package org.smartbit4all.sec.localauth;

import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.sec.authentication.SessionHandlerAuthenticationProvider;
import org.springframework.security.core.Authentication;

public class SessionLocalAuthenticationProvider extends SessionHandlerAuthenticationProvider {

  public SessionLocalAuthenticationProvider(
      LocalAuthenticationProvider localAuthenticationProvider) {
    super(LocalAuthenticationService.KIND, localAuthenticationProvider);
  }

  @Override
  protected User getUserFromAuthentication(Authentication originalAuthentication) {
    return (User) originalAuthentication.getPrincipal();
  }

}
