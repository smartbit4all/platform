package org.smartbit4all.sec.apikey;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

  private static final String DEFAULT_API_KEY_HEADER = "X-API-KEY";

  private String apiKeyHeader = DEFAULT_API_KEY_HEADER;

  private AuthenticationManager authenticationManager;
  private ApiKeyApi apiKeyApi;

  public ApiKeyAuthFilter(AuthenticationManager authenticationManager, ApiKeyApi apiKeyApi) {
    this.authenticationManager = authenticationManager;
    this.apiKeyApi = apiKeyApi;
  }

  @Override
  public void afterPropertiesSet() {
    Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");
    Assert.notNull(this.apiKeyApi, "An ApiKeyApi is required");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    final boolean debug = this.logger.isDebugEnabled();
    try {
      String apiKey = request.getHeader(apiKeyHeader);
      List<URI> scopeUris = apiKeyApi.getMatchingApiKeyScopes(request);

      if (ObjectUtils.isEmpty(apiKey)) {
        throw new BadCredentialsException("Missing API Key");
      }

      Authentication auth = new ApiKeyAuthenticationToken(apiKey, scopeUris);
      Authentication authResult = this.authenticationManager.authenticate(auth);

      if (debug) {
        this.logger.debug("Authentication success: " + authResult);
      }

      SecurityContextHolder.getContext().setAuthentication(authResult);
      onSuccessfulAuthentication(request, response, authResult);
    } catch (AuthenticationException e) {
      SecurityContextHolder.clearContext();

      if (debug) {
        this.logger.debug("Authentication with api key has failed!", e);
      }
      response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
      onUnsuccessfulAuthentication(request, response, e);

      return;
    }

    filterChain.doFilter(request, response);
  }

  protected void onSuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, Authentication authResult) throws IOException {}

  protected void onUnsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) throws IOException {}

  public final void setApiKeyHeader(String apiKeyHeader) {
    Objects.requireNonNull(apiKeyHeader, "apiKeyHeader can not be null!");
    if (apiKeyHeader.isEmpty()) {
      throw new IllegalArgumentException("apiKeyHeader can not be empty!");
    }
    this.apiKeyHeader = apiKeyHeader;
  }

}
