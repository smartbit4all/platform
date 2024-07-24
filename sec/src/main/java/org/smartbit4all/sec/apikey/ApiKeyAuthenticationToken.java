package org.smartbit4all.sec.apikey;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

  private final String apiKey;

  private final List<URI> scopeUris;

  public ApiKeyAuthenticationToken(String apiKey, List<URI> scopeUris,
      Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.apiKey = apiKey;
    this.scopeUris = scopeUris;
    setAuthenticated(false);
  }

  public ApiKeyAuthenticationToken(String apiKey, List<URI> scopeUris) {
    this(apiKey, scopeUris, AuthorityUtils.NO_AUTHORITIES);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return getApiKey();
  }

  public List<URI> getScopeUris() {
    return scopeUris;
  }

  public String getApiKey() {
    return apiKey;
  }
}
