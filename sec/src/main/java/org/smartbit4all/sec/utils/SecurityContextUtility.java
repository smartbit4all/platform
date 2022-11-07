package org.smartbit4all.sec.utils;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.scheduling.SecurityContextScheduling.TechnicalUserProvider;
import org.smartbit4all.sec.token.SessionBasedAuthTokenProvider;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;

public class SecurityContextUtility {

  private SecurityContextUtility() {}

  public static SecurityContext createSecurityContext(UserSessionApi userSessionApi,
      TechnicalUserProvider technicalUserProvider) {
    SecurityContext createEmptyContext = SecurityContextHolder.createEmptyContext();
    User technicalUser = technicalUserProvider.getTechnicalUser();
    Objects.requireNonNull(technicalUser, "technicalUser can not be null!");
    org.smartbit4all.api.session.Session session = userSessionApi.startSession(technicalUser);
    Authentication auth = new UsernamePasswordAuthenticationToken(session, null);
    createEmptyContext.setAuthentication(auth);
    return createEmptyContext;
  }

  public static AbstractAuthenticationToken setSessionAuthenticationTokenInContext(
      HttpServletRequest request, String sessionUriTxt,
      List<SessionBasedAuthTokenProvider> tokenProviders,
      Function<Session, AbstractAuthenticationToken> anonymousAuthTokenProvider,
      SessionManagementApi sessionManagementApi,
      Logger log) {

    Assert.notNull(request, "request cannot be null");
    Assert.notNull(sessionUriTxt, "sessionUriTxt cannot be null");
    Assert.notNull(tokenProviders, "tokenProviders cannot be null");
    if (anonymousAuthTokenProvider == null) {
      anonymousAuthTokenProvider = s -> null;
    }
    if (log == null) {
      log = LoggerFactory.getLogger(SecurityContextUtility.class);
    }
    Session session = sessionManagementApi.initCurrentSession(URI.create(sessionUriTxt));
    if (session != null) {
      log.debug("Session found to set in security context: {}", session);
      log.debug("Looking for a SessionBasedAuthTokenProvider matching the session.");

      SessionBasedAuthTokenProvider tokenProvider = tokenProviders.stream()
          .filter(p -> p.supports(session))
          .findFirst()
          .orElse(null);

      AbstractAuthenticationToken authentication = null;
      if (tokenProvider == null) {
        log.debug(
            "There is no SessionBasedAuthTokenProvider for the given session. Setting anonymous token! session:\n{}",
            session);
        authentication = anonymousAuthTokenProvider.apply(session);
      } else {
        authentication = tokenProvider.getToken(session);
      }

      Objects.requireNonNull(authentication,
          "The provided authentication token can not be null!");

      log.debug("AuthenticationToken has been created for the session with type [{}]",
          authentication.getClass().getName());

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return authentication;
    }
    return null;
  }
}
