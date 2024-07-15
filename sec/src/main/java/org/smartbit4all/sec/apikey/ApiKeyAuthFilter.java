package org.smartbit4all.sec.apikey;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

  public ApiKeyAuthFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  public void setApiKeyHeader(String apiKeyHeader) {
    this.apiKeyHeader = apiKeyHeader;
  }

  @Override
  public void afterPropertiesSet() {
    Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    final boolean debug = this.logger.isDebugEnabled();
    try {
      String apiKey = request.getHeader(apiKeyHeader);
      String apiName = null; // TODO getApiName or path?

      if (ObjectUtils.isEmpty(apiKey)) {
        throw new BadCredentialsException("Missing API Key");
      }

      Authentication auth = new ApiKeyAuthenticationToken(apiKey, apiName);
      Authentication authResult = this.authenticationManager
          .authenticate(auth);

      if (debug) {
        this.logger.debug("Authentication success: " + authResult);
      }

      SecurityContextHolder.getContext().setAuthentication(authResult);
      onSuccessfulAuthentication(request, response, authResult);
    } catch (AuthenticationException failed) {
      SecurityContextHolder.clearContext();

      if (debug) {
        this.logger.debug("Authentication request for failed!", failed);
      }

      onUnsuccessfulAuthentication(request, response, failed);

      return;
    }

    filterChain.doFilter(request, response);
  }

  protected void onSuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, Authentication authResult) throws IOException {}

  protected void onUnsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) throws IOException {}

}
