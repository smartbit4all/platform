package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.security.bean.ApiKey;
import org.smartbit4all.sec.apikey.ApiKeyApi.ApiKeyCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

  private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthenticationProvider.class);

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private ApiKeyApi apiKeyApi;

  private Function<User, Collection<GrantedAuthority>> roleProvider =
      u -> AuthorityUtils.NO_AUTHORITIES;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    ApiKeyAuthenticationToken authToken = (ApiKeyAuthenticationToken) authentication;

    String token = authToken.getApiKey();
    List<URI> scopeUris = authToken.getScopeUris();

    ApiKey apiKey = apiKeyApi.findApiKeyWithToken(token);

    ApiKeyCheckResult checkResult = apiKeyApi.checkApiKey(apiKey, scopeUris);
    if (ApiKeyCheckResult.OK == checkResult) {
      User user = orgApi.getUser(apiKey.getUri());
      return new UsernamePasswordAuthenticationToken(user, "", roleProvider.apply(user));
    }

    log.debug("{} scopeUris: {}", checkResult.details, scopeUris);
    throw new BadCredentialsException(checkResult.details);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(ApiKeyAuthenticationToken.class);
  }

  public void setRoleProvider(Function<User, Collection<GrantedAuthority>> roleProvider) {
    this.roleProvider = roleProvider;
  }
}
