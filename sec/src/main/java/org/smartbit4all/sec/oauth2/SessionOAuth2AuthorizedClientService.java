package org.smartbit4all.sec.oauth2;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.smartbit4all.sec.authprincipal.SessionAuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.util.ObjectUtils;
import io.jsonwebtoken.lang.Assert;

/**
 * Handles the {@link OAuth2AuthorizedClient}s, storing them in the {@link Session}. When a session
 * is not available, it uses the default {@link InMemoryOAuth2AuthorizedClientService}.
 */
public class SessionOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

  private static final Logger log =
      LoggerFactory.getLogger(SessionOAuth2AuthorizedClientService.class);

  private static final String AUTHORIZED_CLIENT_KEY = "OAuth2AuthorizedClient-";

  @Autowired
  private SessionManagementApi sessionManagementApi;

  private final ClientRegistrationRepository clientRegistrationRepository;
  private final InMemoryOAuth2AuthorizedClientService inMemoryClientService;

  public SessionOAuth2AuthorizedClientService(
      ClientRegistrationRepository clientRegistrationRepository) {
    Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
    this.clientRegistrationRepository = clientRegistrationRepository;
    this.inMemoryClientService =
        new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
  }

  @Override
  public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId,
      String principalName) {
    Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
    Assert.hasText(principalName, "principalName cannot be empty");
    Session session = getSessionByPrincipalName(principalName);
    if (session == null) {
      log.debug("The given principalName is not a valid sessionUri! principalName: {}",
          principalName);
      return inMemoryClientService.loadAuthorizedClient(clientRegistrationId, principalName);
    }
    ClientRegistration registration =
        this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
    if (registration == null) {
      log.debug("There is no clientRegistration with clientRegistratioinId [{}]",
          clientRegistrationId);
      return null;
    }

    return sessionManagementApi.getSessionParameterObject(session.getUri(),
        getAuthorizedClientParameterKey(clientRegistrationId));
  }

  @Override
  public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient,
      Authentication principal) {
    Assert.notNull(authorizedClient, "authorizedClient cannot be null");
    Assert.notNull(principal, "principal cannot be null");
    if (principal instanceof SessionAuthToken) {
      SessionAuthPrincipal sessionPrincipal = ((SessionAuthToken) principal).getPrincipal();
      String clientRegistrationId = authorizedClient.getClientRegistration().getRegistrationId();
      sessionManagementApi.setSessionParameterObject(sessionPrincipal.getSessionUri(),
          getAuthorizedClientParameterKey(clientRegistrationId),
          authorizedClient);
    } else {
      inMemoryClientService.saveAuthorizedClient(authorizedClient, principal);
    }
  }

  @Override
  public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
    Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
    Assert.hasText(principalName, "principalName cannot be empty");
    Session session = getSessionByPrincipalName(principalName);
    if (session == null) {
      log.debug("The given principalName is not a valid sessionUri! principalName: {}",
          principalName);
      inMemoryClientService.removeAuthorizedClient(clientRegistrationId, principalName);
    }
    sessionManagementApi.removeSessionParameter(session.getUri(),
        getAuthorizedClientParameterKey(clientRegistrationId));
  }

  private Session getSessionByPrincipalName(String principalName) {
    URI sessionUri = null;
    try {
      sessionUri = URI.create(principalName);
      if (isValidUri(sessionUri)) {
        return sessionManagementApi.readSession(sessionUri);
      }
    } catch (Exception e) {
      // do nothing here when the principalName does not represent a session
    }
    return null;
  }

  private boolean isValidUri(URI sessionUri) {
    return !ObjectUtils.isEmpty(sessionUri.getScheme())
        && !ObjectUtils.isEmpty(sessionUri.getPath());
  }

  private String getAuthorizedClientParameterKey(String clientRegistrationId) {
    return AUTHORIZED_CLIENT_KEY + clientRegistrationId;
  }

}
