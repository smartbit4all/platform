package org.smartbit4all.api.config;

import org.smartbit4all.api.binarydata.BinaryDataMaintenanceApi;
import org.smartbit4all.api.binarydata.BinaryDataMaintenanceApiImpl;
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
public class PlatformApiScheduledConfig {

  @Bean
  public BinaryDataMaintenanceApi binaryDataMaintenanceApi() {
    return new BinaryDataMaintenanceApiImpl();
  }

}
