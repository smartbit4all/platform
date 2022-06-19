package org.smartbit4all.api.navigation.restserver.config;

import org.smartbit4all.api.navigation.restserver.NavigationApiDelegate;
import org.smartbit4all.api.navigation.restserver.impl.NavigationApiDelegateImpl;
import org.smartbit4all.api.restserver.PlatformApiCommonSrvRestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The smartbit4all platform rest server api config.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({PlatformApiCommonSrvRestConfig.class})
public class NavigationSrvRestConfig {

  @Bean
  public NavigationApiDelegate navigationApiDelegate() {
    return new NavigationApiDelegateImpl();
  }

}
