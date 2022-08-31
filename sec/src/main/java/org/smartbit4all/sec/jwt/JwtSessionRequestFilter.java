package org.smartbit4all.sec.jwt;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.smartbit4all.sec.token.SessionBasedAuthTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This request filter tries to catch a JWT token passed as an 'Authentication: Bearer' header or as
 * a cookie. Extracting the token it looks for a smartbit4all session uri which then can be loaded
 * and set to the Spring security context.
 * <hr>
 * To set the {@link Authentication} to the spring context <b><u>IT IS REQUIRED TO SET AT LEAST ONE
 * {@link SessionBasedAuthTokenProvider}</u></b> with the
 * {@link #addSessionBasedAuthTokenProvider(SessionBasedAuthTokenProvider)} method!
 */
public class JwtSessionRequestFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(JwtSessionRequestFilter.class);

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private JwtUtil jwtUtil;

  private List<SessionBasedAuthTokenProvider> authTokenProviders = new ArrayList<>();
  private Function<Session, AbstractAuthenticationToken> anonymousAuthTokenProvider =
      session -> createAnonymousAuthToken(session.getUri());

  public JwtSessionRequestFilter() {}

  public JwtSessionRequestFilter(List<SessionBasedAuthTokenProvider> authTokenProviders) {
    if (ObjectUtils.isEmpty(authTokenProviders)) {
      throw new IllegalArgumentException("authTokenProviders can not be null nor empty!");
    }
    this.authTokenProviders = authTokenProviders;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    /**
     * This method filters the incoming REST API calls by the authentication in the header. If the
     * header contains a valid JwtToken (it is not expired and a session uri can be decrypted) the
     * call continues to execution. However, if the JwtToken is not valid, it throws an exception
     * and the call would not be executed.
     */

    String jwt = jwtUtil.getJwtTokenFromRequest(request);
    if (ObjectUtils.isEmpty(jwt)) {
      log.warn("The incoming request does not contain a jwt token!");
    } else {
      if (jwtUtil.isTokenExpired(jwt)) {
        log.warn(
            "The jwt token has expired! The authentication token can not be set to the security context!");
      } else {
        String sessionUriTxt = jwtUtil.extractSubject(jwt);
        if (sessionUriTxt != null
            && SecurityContextHolder.getContext().getAuthentication() == null) {
          setAuthToken(request, sessionUriTxt);
        }
      }
    }

    filterChain.doFilter(request, response);
  }

  private void setAuthToken(HttpServletRequest request, String sessionUriTxt) {
    Session session =
        sessionApi.readSession(URI.create(sessionUriTxt));
    if (session != null) {
      log.debug("Session found to set in security context: {}", session);
      log.debug("Looking for a SessionBasedAuthTokenProvider matching the session.");

      SessionBasedAuthTokenProvider tokenProvider = authTokenProviders.stream()
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
    }
  }

  /**
   * Registers a {@link SessionBasedAuthTokenProvider} to the filter.
   */
  public void addSessionBasedAuthTokenProvider(SessionBasedAuthTokenProvider authProvider) {
    Objects.requireNonNull(authProvider, "authProvider can not be null!");
    authTokenProviders.add(authProvider);
  }

  public void setAnonymousAuthTokenProvider(
      Function<Session, AbstractAuthenticationToken> anonymousAuthTokenProvider) {
    Objects.requireNonNull(anonymousAuthTokenProvider,
        "anonymousAuthTokenProvider can not be null!");
    this.anonymousAuthTokenProvider = anonymousAuthTokenProvider;
  }

  private AbstractAuthenticationToken createAnonymousAuthToken(URI sessionUri) {
    SessionAuthPrincipal sessionPrincipal = SessionAuthPrincipal.of(sessionUri);
    return new AnonymousAuthenticationToken(
        "anonymous", sessionPrincipal, AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
  }

}
