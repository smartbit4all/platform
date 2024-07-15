package org.smartbit4all.sec.apikey;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

  private final String apiKey;

  private final String apiName;

  public ApiKeyAuthenticationToken(String apiKey, String apiName,
      Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.apiKey = apiKey;
    this.apiName = apiName;
    setAuthenticated(false);
  }

  public ApiKeyAuthenticationToken(String apiKey, String apiName) {
    this(apiKey, apiName, AuthorityUtils.NO_AUTHORITIES);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return getApiKey();
  }

  public String getApiName() {
    return apiName;
  }

  public String getApiKey() {
    return apiKey;
  }
}
