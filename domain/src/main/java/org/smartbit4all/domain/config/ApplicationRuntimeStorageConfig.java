package org.smartbit4all.domain.config;

import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.smartbit4all.domain.application.ApplicationRuntimeApiStorageImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The smartbit4all platform api config for scheduled apis.
 * 
 * @author Peter Boros
 */
@Configuration
@EnableScheduling
public class ApplicationRuntimeStorageConfig {

  @Bean
  public ApplicationRuntimeApi applicationRuntimeApi() {
    return new ApplicationRuntimeApiStorageImpl();
  }

}
