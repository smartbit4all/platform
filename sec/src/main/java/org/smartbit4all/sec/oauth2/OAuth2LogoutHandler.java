package org.smartbit4all.sec.oauth2;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.sec.authprincipal.SessionAuthToken;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This logout handler removes the AuthInfo from the session. Also removes the AuthorizedClient.
 */
public class OAuth2LogoutHandler implements LogoutHandler, InitializingBean {

  private final String clientRegistrationId;

  @Autowired
  private OAuth2AuthorizedClientRepository authorizedClientRepository;

  @Autowired
  private ClientRegistrationRepository clientRegistrationRepository;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  private boolean disableOicdClientLogout = false;

  private OidcClientLogoutHandler oidcClientLogoutHandler;


  public OAuth2LogoutHandler(String clientRegistrationId) {
    this.clientRegistrationId = clientRegistrationId;
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    if (!disableOicdClientLogout && oidcClientLogoutHandler != null
        && oidcClientLogoutHandler.canLogout()) {
      oidcClientLogoutHandler.handleLogout(request, response, authentication);
    }
    sessionManagementApi.removeSessionAuthentication(sessionApi.getSessionUri(),
        OAuth2AuthenticationDataProvider.getKind(clientRegistrationId));
    authorizedClientRepository.removeAuthorizedClient(clientRegistrationId, authentication, request,
        response);
  }

  public boolean isDisableOicdClientLogout() {
    return disableOicdClientLogout;
  }

  public void setDisableOicdClientLogout(boolean disableOicdClientLogout) {
    this.disableOicdClientLogout = disableOicdClientLogout;
  }

  public OidcClientLogoutHandler getOidcClientLogoutHandler() {
    return oidcClientLogoutHandler;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.oidcClientLogoutHandler = new OidcClientLogoutHandler(clientRegistrationId,
        clientRegistrationRepository, sessionApi);
  }

  /**
   * Customized based on {@link OidcClientInitiatedLogoutSuccessHandler}
   */
  public static class OidcClientLogoutHandler
      extends AbstractAuthenticationTargetUrlRequestHandler {

    private static final Logger log =
        LoggerFactory.getLogger(OAuth2LogoutHandler.OidcClientLogoutHandler.class);

    private final SessionApi sessionApi;
    private final String clientRegistrationId;
    private final URI endSessionEndpoint;

    private String postLogoutRedirectUri;// = "/login";

    public OidcClientLogoutHandler(String clientRegistrationId,
        ClientRegistrationRepository clientRegistrationRepository,
        SessionApi sessionApi) {
      Objects.requireNonNull(clientRegistrationId, "clientRegistrationId can not be null!");
      Objects.requireNonNull(clientRegistrationRepository,
          "clientRegistrationRepository can not be null!");
      Objects.requireNonNull(sessionApi,
          "sessionApi can not be null!");

      this.sessionApi = sessionApi;
      this.clientRegistrationId = clientRegistrationId;
      ClientRegistration clientRegistration =
          clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
      this.endSessionEndpoint = endSessionEndpoint(clientRegistration);

      if (!canLogout()) {
        log.warn(
            "OidcClientLogoutHandler has been set up for client id [{}], but the logout endpoint could not be set up!",
            clientRegistrationId);
      }
    }

    public boolean canLogout() {
      return endSessionEndpoint != null;
    }

    public void handleLogout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
      try {
        super.handle(request, response, authentication);
      } catch (IOException | ServletException e) {
        log.error("An error occured during client initialized oidc session logout!", e);
      }
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request,
        HttpServletResponse response, Authentication authentication) {
      String targetUrl = null;
      if (endSessionEndpoint == null) {
        return "";
      }

      if (authentication instanceof SessionAuthToken) {
        AccountInfo accountInfo = sessionApi
            .getAuthentication(OAuth2AuthenticationDataProvider.getKind(clientRegistrationId));
        String idToken =
            accountInfo == null ? null
                : accountInfo.getParameters()
                    .get(OAuth2SessionAuthSuccessHandler.AI_PARAM_ID_TOKEN);
        if (idToken == null) {
          log.debug(
              "Could not get the idToken for logout. clientRegistrationId: [{}]",
              clientRegistrationId);
          return "";
        }

        URI postLogoutRedirectUriByRequest = postLogoutRedirectUri(request);
        targetUrl = endpointUri(endSessionEndpoint, idToken, postLogoutRedirectUriByRequest);
      }
      if (targetUrl == null) {
        targetUrl = super.determineTargetUrl(request, response);
      }

      return targetUrl;
    }

    private URI endSessionEndpoint(ClientRegistration clientRegistration) {
      URI result = null;
      if (clientRegistration != null) {
        Object endSessionEndpointUrl =
            clientRegistration.getProviderDetails().getConfigurationMetadata()
                .get("end_session_endpoint");
        if (endSessionEndpointUrl != null) {
          result = URI.create(endSessionEndpointUrl.toString());
        }
      }

      return result;
    }

    private URI postLogoutRedirectUri(HttpServletRequest request) {
      if (this.postLogoutRedirectUri == null) {
        return null;
      }
      UriComponents uriComponents =
          UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
              .replacePath(request.getContextPath())
              .replaceQuery(null)
              .fragment(null)
              .build();
      return UriComponentsBuilder.fromUriString(this.postLogoutRedirectUri)
          .buildAndExpand(Collections.singletonMap("baseUrl", uriComponents.toUriString()))
          .toUri();
    }


    private String endpointUri(URI endSessionEndpoint, String idToken, URI postLogoutRedirectUri) {
      UriComponentsBuilder builder = UriComponentsBuilder.fromUri(endSessionEndpoint);
      builder.queryParam("id_token_hint", idToken);
      if (postLogoutRedirectUri != null) {
        builder.queryParam("post_logout_redirect_uri", postLogoutRedirectUri);
      }
      return builder.encode(StandardCharsets.UTF_8).build().toUriString();
    }

    /**
     * Set the post logout redirect uri template to use. Supports the {@code "{baseUrl}"}
     * placeholder, for example:
     *
     * <pre>
     * handler.setPostLogoutRedirectUriTemplate("{baseUrl}");
     * </pre>
     *
     * will make so that {@code post_logout_redirect_uri} will be set to the base url for the client
     * application.
     *
     * @param postLogoutRedirectUri - A template for creating the {@code post_logout_redirect_uri}
     *        query parameter
     */
    public void setPostLogoutRedirectUri(String postLogoutRedirectUri) {
      Assert.notNull(postLogoutRedirectUri, "postLogoutRedirectUri cannot be null");
      this.postLogoutRedirectUri = postLogoutRedirectUri;
    }

  }

}
