package org.smartbit4all.sec.oauth2.mdm;

import java.util.List;
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
public class DynamicOAuth2LogoutHandler implements LogoutHandler {


  @Autowired
  private OAuth2AuthorizedClientRepository authorizedClientRepository;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  private DynamicOidcClientLogoutHandler oidcClientLogoutHandler;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    oidcClientLogoutHandler.handleLogout(request, response, authentication);

    List<String> dynamicOAuthRegistrationIdsOfSession =
        DynamicOAuth2Helper.getOAuth2ClientRegIdsOfSession(sessionApi);
    dynamicOAuthRegistrationIdsOfSession.forEach(clientRegistrationId -> {

      sessionManagementApi.removeSessionAuthentication(sessionApi.getSessionUri(),
          DynamicOAuth2Helper.getAuthInfoKind(clientRegistrationId));
      authorizedClientRepository.removeAuthorizedClient(clientRegistrationId, authentication,
          request,
          response);
    });
  }
}
