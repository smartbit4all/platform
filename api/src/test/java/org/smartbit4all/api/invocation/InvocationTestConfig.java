package org.smartbit4all.api.invocation;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class})
public class InvocationTestConfig {

  @Bean
  public TestApi testAPi() {
    return new TestApiImpl();
  }

  @Bean
  public TestPrimaryApi primaryApi() {
    return new TestPrimaryApiImpl(TestPrimaryApi.class, TestContributionApi.class);
  }

  @Bean
  public TestContributionApi contributionApi1() {
    return new TestContributionApiImpl("contributionApi1");
  }

  @Bean
  public TestContributionApi contributionApi2() {
    return new TestContributionApiImpl("contributionApi2");
  }

  @Bean
  ObjectStorageInMemory createInMemoryStorage(ObjectApi objectApi) {
    return new ObjectStorageInMemory(objectApi);
  }

}
