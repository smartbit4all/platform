package org.smartbit4all.api.config;

import org.smartbit4all.api.binarydata.BinaryDataMaintenanceApi;
import org.smartbit4all.api.binarydata.BinaryDataMaintenanceApiImpl;
import org.smartbit4all.api.invocation.InvocationRegisterApi;
import org.smartbit4all.api.invocation.InvocationRegisterApiIml;
import org.smartbit4all.domain.config.ApplicationRuntimeStorageConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The smartbit4all platform api config for scheduled apis.
 *
 * @author Peter Boros
 */
@Configuration
@EnableScheduling
@Import(ApplicationRuntimeStorageConfig.class)
public class PlatformApiScheduledConfig {

  @Bean
  public BinaryDataMaintenanceApi binaryDataMaintenanceApi() {
    return new BinaryDataMaintenanceApiImpl();
  }

  @Bean
  public InvocationRegisterApi invocationRegisterApi() {
    return new InvocationRegisterApiIml();
  }

}
