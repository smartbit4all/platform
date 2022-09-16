package org.smartbit4all.sec.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * This logout handler removes the AuthInfo from the session. Also removes the AuthorizedClient.
 */
public class OAuth2LogoutHandler implements LogoutHandler {

  private final String clientRegistrationId;

  @Autowired
  private OAuth2AuthorizedClientRepository authorizedClientRepository;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  public OAuth2LogoutHandler(String clientRegistrationId) {
    this.clientRegistrationId = clientRegistrationId;
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    sessionManagementApi.removeSessionAuthentication(sessionApi.getSessionUri(),
        OAuth2AuthenticationDataProvider.getKind(clientRegistrationId));
    authorizedClientRepository.removeAuthorizedClient(clientRegistrationId, authentication, request,
        response);
  }

}
