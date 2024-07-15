package org.smartbit4all.sec.apikey;

import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.sec.authentication.SessionHandlerAuthenticationProvider;
import org.springframework.security.core.Authentication;

public class SessionApiKeyAuthenticationProvider extends SessionHandlerAuthenticationProvider {

  public SessionApiKeyAuthenticationProvider(
      ApiKeyAuthenticationProvider apiKeyAuthenticationProvider) {
    super("apiKeyAuthentication", apiKeyAuthenticationProvider);
  }

  @Override
  protected User getUserFromAuthentication(Authentication originalAuthentication) {
    return (User) originalAuthentication.getPrincipal();
  }

}
