package org.smartbit4all.api.config;

import org.smartbit4all.api.runtime.ApplicationRuntimeApi;
import org.smartbit4all.api.runtime.ApplicationRuntimeApiStorageImpl;
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
