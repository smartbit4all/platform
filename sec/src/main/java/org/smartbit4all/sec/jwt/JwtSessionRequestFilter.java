package org.smartbit4all.sec.jwt;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
import org.smartbit4all.api.session.exception.ExpiredSessionException;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.sec.authentication.DefaultAuthTokenProvider;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.smartbit4all.sec.token.SessionBasedAuthTokenProvider;
import org.smartbit4all.sec.utils.SecurityContextUtility;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
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
public class JwtSessionRequestFilter extends OncePerRequestFilter implements InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(JwtSessionRequestFilter.class);

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired(required = false)
  private ViewContextService viewContextService;

  @Autowired
  private JwtUtil jwtUtil;

  @Value("${openapi.session.base-path:}")
  private String sessionPath;

  private List<SessionBasedAuthTokenProvider> authTokenProviders =
      Arrays.asList(new DefaultAuthTokenProvider());
  private Function<Session, AbstractAuthenticationToken> anonymousAuthTokenProvider =
      session -> createAnonymousAuthToken(session.getUri());

  private final List<CallDefinition> skippableCalls = new ArrayList<>();

  private final List<CallDefinition> notSkippableCalls = new ArrayList<>();

  public JwtSessionRequestFilter() {}

  public JwtSessionRequestFilter(SessionBasedAuthTokenProvider... authTokenProviders) {
    this();
    if (ObjectUtils.isEmpty(authTokenProviders)) {
      throw new IllegalArgumentException("authTokenProviders can not be null nor empty!");
    }
    this.authTokenProviders = Arrays.asList(authTokenProviders);
  }

  @Override
  public void afterPropertiesSet() throws ServletException {
    super.afterPropertiesSet();
    skippableCalls.add(new CallDefinition(HttpMethod.POST, sessionPath + "/refresh"));
    skippableCalls.add(new CallDefinition(HttpMethod.PUT, sessionPath + "/session"));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    /*
     * This method filters the incoming REST API calls by the authentication in the header. If the
     * header contains a valid JwtToken (it is not expired and a session uri can be decrypted) the
     * call continues to execution. However, if the JwtToken is not valid, it throws an exception
     * and the call would not be executed.
     */

    if (callWithoutSessionToken(request)) {
      // let through the specified requests without jwt validation
      filterChain.doFilter(request, response);
      return;
    }

    String jwt = jwtUtil.getJwtTokenFromRequest(request);

    if (ObjectUtils.isEmpty(jwt)) {
      log.debug("The incoming request does not contain a jwt token!");
    } else {
      if (jwtUtil.isTokenExpired(jwt)) {
        log.debug(
            "The jwt token has expired! The authentication token can not be set to the security context!");
        throw new ExpiredSessionException();
      } else {
        String sessionUriTxt = jwtUtil.extractSubject(jwt);
        if (sessionUriTxt != null
            && SecurityContextHolder.getContext().getAuthentication() == null) {
          setAuthToken(request, sessionUriTxt);
        }
      }
    }

    handleViewContextAndFilterChain(filterChain, request, response);

  }

  private boolean callWithoutSessionToken(HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    String method = request.getMethod();
    boolean isSkippable = skippableCalls.stream().anyMatch(
        sc -> {
          return requestURI.contains(sc.path)
              && sc.method.matches(method);
        });

    boolean notSkippable = notSkippableCalls.stream().anyMatch(
        sc -> {
          return requestURI.contains(sc.path)
              && sc.method.matches(method);
        });

    return isSkippable || !notSkippable;
  }

  private void handleViewContextAndFilterChain(FilterChain filterChain, HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    if (viewContextService != null) {
      final String uuid = request.getHeader("viewContextUuid");
      if (!ObjectUtils.isEmpty(uuid)) {
        try {
          viewContextService.execute(
              UUID.fromString(uuid),
              () -> filterChain.doFilter(request, response));
        } catch (Exception e) {
          throw new ServletException("Error when executing viewContext process", e);
        }
      } else {
        log.debug("viewContextUUid not received!");
        filterChain.doFilter(request, response);
      }
    } else {
      filterChain.doFilter(request, response);
    }
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

  public void addSkippableCall(HttpMethod method, String path) {
    skippableCalls.add(new CallDefinition(method, path));
  }

  public void addNotSkippableCall(HttpMethod method, String path) {
    notSkippableCalls.add(new CallDefinition(method, path));
  }

  private static class CallDefinition {

    private HttpMethod method;
    private String path;

    public CallDefinition(HttpMethod method, String path) {
      Assert.notNull(method, "method cannot be null");
      Assert.notNull(path, "path cannot be null");
      this.method = method;
      this.path = path;
    }

  }

}
