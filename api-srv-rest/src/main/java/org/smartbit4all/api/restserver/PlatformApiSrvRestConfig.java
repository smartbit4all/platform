package org.smartbit4all.api.restserver;

import org.smartbit4all.api.utils.RestUtilsApi;
import org.smartbit4all.api.utils.RestUtilsApiImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The smartbit4all platform api config.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({PlatformApiCommonSrvRestConfig.class})
public class PlatformApiSrvRestConfig {

  @Bean
  RestUtilsApi restUtilsApi() {
    return new RestUtilsApiImpl();
  }

}
