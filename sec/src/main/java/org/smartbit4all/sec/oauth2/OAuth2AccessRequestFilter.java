package org.smartbit4all.sec.oauth2;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class OAuth2AccessRequestFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(OAuth2AccessRequestFilter.class);

  private String clientRegistrationId;

  @Autowired
  private OAuth2AuthorizedClientManager authorizedClientManager;

  @Autowired
  private SessionApi sessionApi;


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
        Session session = sessionApi.currentSession();
        sessionApi.removeSessionAuthentication(session.getUri(),
            OAuth2AuthenticationDataProvider.getKind(clientRegistrationId));
      }
    }


    filterChain.doFilter(request, response);
  }

  private boolean isOAuth() {
    Session session = null;
    try {
      session = sessionApi.currentSession();
    } catch (Exception e) {
      return false;
    }
    if (session == null || ObjectUtils.isEmpty(session.getAuthentications())) {
      return false;
    }
    return session.getAuthentications().stream()
        .anyMatch(ai -> ai.getKind().contains(clientRegistrationId));
  }

}
