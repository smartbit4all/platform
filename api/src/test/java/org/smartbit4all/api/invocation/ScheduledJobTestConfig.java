package org.smartbit4all.api.invocation;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.bean.ScheduledTestApiImpl;
import org.smartbit4all.api.invocation.config.InvocationApiMdmConfig;
import org.smartbit4all.domain.config.ApplicationRuntimeStorageConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class, ApplicationRuntimeStorageConfig.class,
    InvocationApiMdmConfig.class})
public class ScheduledJobTestConfig {

  @Bean
  public ScheduledJobManager scheduledJobManager() {
    return new ScheduledJobManager();
  }

  @Bean
  public ScheduledTestApi scheduledTestApi() {
    return new ScheduledTestApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<ScheduledTestApi> scheduledTestApiProvider(
      ScheduledTestApi scheduledTestApi) {
    return ProviderApiInvocationHandler.providerOf(ScheduledTestApi.class, scheduledTestApi);
  }
}
