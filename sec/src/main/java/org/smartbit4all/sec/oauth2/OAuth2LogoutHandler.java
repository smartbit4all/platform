package org.smartbit4all.sec.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class OAuth2LogoutHandler implements LogoutHandler {

  private static final Logger log = LoggerFactory.getLogger(OAuth2LogoutHandler.class);

  private final String clientRegistrationId;

  @Autowired
  private OAuth2AuthorizedClientRepository authorizedClientRepository;

  @Autowired
  private SessionApi sessionApi;

  public OAuth2LogoutHandler(String clientRegistrationId) {
    this.clientRegistrationId = clientRegistrationId;
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    Session session = sessionApi.currentSession();
    sessionApi.removeSessionAuthentication(session.getUri(),
        OAuth2AuthenticationDataProvider.getKind(clientRegistrationId));
    authorizedClientRepository.removeAuthorizedClient(clientRegistrationId, authentication, request,
        response);
  }

}
