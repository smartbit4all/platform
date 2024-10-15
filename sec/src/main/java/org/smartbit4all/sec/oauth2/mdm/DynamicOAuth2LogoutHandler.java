package org.smartbit4all.sec.oauth2.mdm;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * This logout handler removes the AuthInfo from the session. Also removes the AuthorizedClient.
 */
public class DynamicOAuth2LogoutHandler implements LogoutHandler {


  @Autowired
  private OAuth2AuthorizedClientRepository authorizedClientRepository;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  private DynamicOidcClientLogoutHandler oidcClientLogoutHandler;

  @Autowired
  private DynamicOAuth2PropertiesApi dynamicOAuth2PropertiesApi;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    oidcClientLogoutHandler.handleLogout(request, response, authentication);

    List<String> dynamicOAuthRegistrationIdsOfSession =
        dynamicOAuth2PropertiesApi.getOAuth2ClientRegIdsOfSession();
    dynamicOAuthRegistrationIdsOfSession.forEach(clientRegistrationId -> {

      sessionManagementApi.removeSessionAuthentication(sessionApi.getSessionUri(),
          DynamicOAuth2PropertiesApi.getAuthInfoKind(clientRegistrationId));
      authorizedClientRepository.removeAuthorizedClient(clientRegistrationId, authentication,
          request,
          response);
    });
  }
}
