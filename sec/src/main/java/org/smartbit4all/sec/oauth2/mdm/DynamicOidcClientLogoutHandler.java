package org.smartbit4all.sec.oauth2.mdm;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.security.bean.OAuthClientProperties;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.sec.authprincipal.SessionAuthToken;
import org.smartbit4all.sec.oauth2.OAuth2SessionAuthSuccessHandler;
import org.smartbit4all.sec.oauth2.OidcClientLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Customized based on {@link OidcClientInitiatedLogoutSuccessHandler} <br/>
 * Works as {@link OidcClientLogoutHandler} but handles dynamic OAuth2 client registrations.
 */
public class DynamicOidcClientLogoutHandler extends AbstractAuthenticationTargetUrlRequestHandler {

  private static final Logger log =
      LoggerFactory.getLogger(DynamicOidcClientLogoutHandler.class);

  @Autowired
  private SessionApi sessionApi;
  @Autowired
  private ClientRegistrationRepository clientRegistrationRepository;
  @Autowired
  private DynamicOAuth2PropertiesApi dynamicOAuth2PropertiesApi;

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

    List<String> dynamicOAuthRegistrationIdsOfSession =
        dynamicOAuth2PropertiesApi.getOAuth2ClientRegIdsOfSession();
    Iterator<String> regIdIter = dynamicOAuthRegistrationIdsOfSession.iterator();
    String targetUrl = null;
    while (regIdIter.hasNext() && targetUrl == null) {
      String clientRegistrationId = regIdIter.next();

      OAuthClientProperties clientProperties =
          dynamicOAuth2PropertiesApi.getClientPropsForRegId(clientRegistrationId);

      URI endSessionEndpoint = endSessionEndpoint(clientRegistrationId, clientProperties);
      if (endSessionEndpoint == null) {
        return "";
      }

      if (authentication instanceof SessionAuthToken) {
        AccountInfo accountInfo = sessionApi
            .getAuthentication(DynamicOAuth2PropertiesApi.getAuthInfoKind(clientRegistrationId));
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

        URI postLogoutRedirectUriByRequest = postLogoutRedirectUri(request, clientProperties);
        targetUrl = endpointUri(endSessionEndpoint, idToken, postLogoutRedirectUriByRequest);
      }
    }
    if (targetUrl == null) {
      targetUrl = super.determineTargetUrl(request, response);
    }

    return targetUrl;
  }

  private URI endSessionEndpoint(String clientRegistrationId,
      OAuthClientProperties clientProperties) {
    ClientRegistration clientRegistration =
        clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
    if (clientRegistration != null) {
      Object endSessionEndpointUrl =
          clientRegistration.getProviderDetails().getConfigurationMetadata()
              .get("end_session_endpoint");
      if (endSessionEndpointUrl != null) {
        return URI.create(endSessionEndpointUrl.toString());
      }
    }

    String explAuthServerEndSessionEndpoint = clientProperties.getLogoutEndSessionEndpoint();
    if (!ObjectUtils.isEmpty(explAuthServerEndSessionEndpoint)) {
      return URI.create(explAuthServerEndSessionEndpoint);
    }

    return null;
  }

  /**
   * Set the post logout redirect uri template to use. Supports the {@code "{baseUrl}"} placeholder,
   * for example:
   *
   * <pre>
   * handler.setPostLogoutRedirectUriTemplate("{baseUrl}");
   * </pre>
   *
   * will make so that {@code post_logout_redirect_uri} will be set to the base url for the client
   * application.
   *
   */
  private URI postLogoutRedirectUri(HttpServletRequest request,
      OAuthClientProperties clientProperties) {
    String postLogoutRedirectUri = clientProperties.getLogoutRedirectPath();
    if (ObjectUtils.isEmpty(postLogoutRedirectUri)) {
      return null;
    }
    UriComponents uriComponents =
        UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
            .replacePath(request.getContextPath())
            .replaceQuery(null)
            .fragment(null)
            .build();
    return UriComponentsBuilder.fromUriString(postLogoutRedirectUri)
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



}
