package org.smartbit4all.domain.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.smartbit4all.domain.application.ApplicationRuntimeApiStorageImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * The smartbit4all platform api config for scheduled apis.
 * 
 * @author Peter Boros
 */
@Configuration
@EnableScheduling
public class ApplicationRuntimeStorageConfig implements SchedulingConfigurer {

  @Bean
  public ApplicationRuntimeApi applicationRuntimeApi() {
    return new ApplicationRuntimeApiStorageImpl();
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {}

  @Bean(destroyMethod = "shutdown")
  public Executor taskExecutor() {
    return Executors.newScheduledThreadPool(5);
  }


}
