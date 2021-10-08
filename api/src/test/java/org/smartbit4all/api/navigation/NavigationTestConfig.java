package org.smartbit4all.api.navigation;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PlatformApiConfig.class)
public class NavigationTestConfig {

  @Bean
  Navigation1 navigation1() {
    return new Navigation1();
  }

  @Bean
  Navigation2 navigation2() {
    return new Navigation2(Navigation2.NAV_NAME);
  }

  @Bean
  Navigation3 navigation3() {
    return new Navigation3(Navigation3.NAV_NAME);
  }
}
