package org.smartbit4all.testing.platformevent;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.platformevent.PlatformEventContributionApi;
import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.sec.config.SecurityLocalTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class, TestFSConfig.class, SecurityLocalTestConfig.class})
public class PlatformEventApiTestConfig {

  @Bean
  public PlatformEventContributionApi platformEventApiTestContributor() {
    return new PlatformEventApiTestContributor();
  }
}
