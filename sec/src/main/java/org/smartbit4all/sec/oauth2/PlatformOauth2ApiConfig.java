package org.smartbit4all.sec.oauth2;

import org.smartbit4all.sec.oauth2.mdm.DynamicOAuth2PropertiesApi;
import org.smartbit4all.sec.oauth2.mdm.DynamicOAuth2PropertiesApiImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformOauth2ApiConfig {

  @Bean
  DynamicOAuth2PropertiesApi dynamicOAuth2PropertiesApi() {
    return new DynamicOAuth2PropertiesApiImpl();
  }

}
