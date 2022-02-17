package org.smartbit4all.ui.api.navigation.restserver.config;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.ui.api.navigation.restserver.ViewModelApiDelegate;
import org.smartbit4all.ui.api.navigation.restserver.impl.ViewModelApiDelegateImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * UI Api rest server config.
 * 
 */
@Configuration
@Import({PlatformApiConfig.class})
public class UIApiSrvRestConfig {

  @Bean
  public ViewModelApiDelegate invocationApiDelegate() {
    return new ViewModelApiDelegateImpl();
  }

}
