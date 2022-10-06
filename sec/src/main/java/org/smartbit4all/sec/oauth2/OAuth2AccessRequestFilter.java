package org.smartbit4all.sec.oauth2;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.exception.NoCurrentSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authorizes the OAuth2AuthorizedClient stored in the session on each incoming request. Also
 * handling the token refresh when the refresh token available.
 */
public class OAuth2AccessRequestFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(OAuth2AccessRequestFilter.class);

  private String clientRegistrationId;

  @Autowired
  private OAuth2AuthorizedClientManager authorizedClientManager;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;


  public OAuth2AccessRequestFilter(String clientRegistrationId) {
    Assert.notNull(clientRegistrationId, "clientRegistrationId can not be null!");
    this.clientRegistrationId = clientRegistrationId;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (isOAuth()) {
      Authentication principal = SecurityContextHolder.getContext().getAuthentication();
      OAuth2AuthorizeRequest authorizeRequest =
          OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
              .principal(principal)
              .attribute(HttpServletRequest.class.getName(), request)
              .attribute(HttpServletResponse.class.getName(), response)
              .build();
      try {
        this.authorizedClientManager.authorize(authorizeRequest);
      } catch (OAuth2AuthorizationException ex) {
        log.debug("OAuth authorization failed on service access.");
        sessionManagementApi.removeSessionAuthentication(sessionApi.getSessionUri(),
            getAuthInfoKind());
      }
    }


    filterChain.doFilter(request, response);
  }

  private boolean isOAuth() {
    try {
      AccountInfo authInfo = sessionApi.getAuthentication(getAuthInfoKind());
      return authInfo != null;
    } catch (NoCurrentSessionException e) {
      return false;
    }
  }

  private String getAuthInfoKind() {
    return OAuth2AuthenticationDataProvider.getKind(clientRegistrationId);
  }

}
