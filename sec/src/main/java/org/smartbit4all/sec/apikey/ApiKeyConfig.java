package org.smartbit4all.sec.apikey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiKeyConfig {

  @Bean
  public ApiKeyApi apiKeyApi() {
    return new ApiKeyApiImpl();
  }

  @Bean
  public ApiKeyInnerApi apiKeyInnerApi() {
    return new ApiKeyInnerApiImpl();
  }

  @Bean
  public ApiKeyAuthenticationProvider apiKeyAuthenticationProvider() {
    return new ApiKeyAuthenticationProvider();
  }

  @Bean
  public SessionApiKeyAuthenticationProvider sessionApiKeyAuthenticationProvider() {
    return new SessionApiKeyAuthenticationProvider(apiKeyAuthenticationProvider());
  }

}
