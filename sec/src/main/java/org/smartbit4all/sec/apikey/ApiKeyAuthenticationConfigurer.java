package org.smartbit4all.sec.apikey;

import java.util.Objects;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.util.ObjectUtils;

public class ApiKeyAuthenticationConfigurer<B extends HttpSecurityBuilder<B>> extends
    AbstractHttpConfigurer<ApiKeyAuthenticationConfigurer<B>, B> {

  private String apiKeyHeaderKey;
  private ApiKeyApi apiKeyApi;

  public ApiKeyAuthenticationConfigurer(ApiKeyApi apiKeyApi) {
    Objects.requireNonNull(apiKeyApi, "apiKeyApi can not be null!");
    this.apiKeyApi = apiKeyApi;
  }

  @Override
  public void configure(B http) {
    AuthenticationManager authenticationManager = http
        .getSharedObject(AuthenticationManager.class);
    ApiKeyAuthFilter apiKeyAuthFilter = new ApiKeyAuthFilter(authenticationManager, apiKeyApi);
    if (!ObjectUtils.isEmpty(apiKeyHeaderKey)) {
      apiKeyAuthFilter.setApiKeyHeader(apiKeyHeaderKey);
    }
    apiKeyAuthFilter = postProcess(apiKeyAuthFilter);
    http.addFilterBefore(apiKeyAuthFilter, RequestCacheAwareFilter.class);
  }

  public ApiKeyAuthenticationConfigurer<B> apiKeyHeaderKey(String apiKeyHeaderKey) {
    Objects.requireNonNull(apiKeyHeaderKey, "apiKeyHeaderKey can not be null!");
    this.apiKeyHeaderKey = apiKeyHeaderKey;
    return this;
  }

}
