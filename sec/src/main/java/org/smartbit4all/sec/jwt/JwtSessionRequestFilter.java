package org.smartbit4all.sec.jwt;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.sec.authentication.DefaultAuthTokenProvider;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.smartbit4all.sec.token.SessionBasedAuthTokenProvider;
import org.smartbit4all.sec.utils.SecurityContextUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private SessionManagementApi sessionManagementApi;

  @Autowired(required = false)
  private ViewContextService viewContextService;

  @Autowired
  private JwtUtil jwtUtil;

  private List<SessionBasedAuthTokenProvider> authTokenProviders =
      Arrays.asList(new DefaultAuthTokenProvider());
  private Function<Session, AbstractAuthenticationToken> anonymousAuthTokenProvider =
      session -> createAnonymousAuthToken(session.getUri());

  public JwtSessionRequestFilter() {}

  public JwtSessionRequestFilter(SessionBasedAuthTokenProvider... authTokenProviders) {
    if (ObjectUtils.isEmpty(authTokenProviders)) {
      throw new IllegalArgumentException("authTokenProviders can not be null nor empty!");
    }
    this.authTokenProviders = Arrays.asList(authTokenProviders);
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
      log.debug("The incoming request does not contain a jwt token!");
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
    if (viewContextService != null) {
      final String uuid = request.getHeader("viewContextUuid");
      if (uuid != null) {
        viewContextService.setCurrentViewContext(UUID.fromString(uuid));
      } else {
        viewContextService.setCurrentViewContext(null);
      }
    }

    filterChain.doFilter(request, response);
  }

  private void setAuthToken(HttpServletRequest request, String sessionUriTxt) {
    SecurityContextUtility.setSessionAuthenticationTokenInContext(request, sessionUriTxt,
        authTokenProviders,
        anonymousAuthTokenProvider, sessionManagementApi, log);
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
