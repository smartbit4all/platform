package org.smartbit4all.api.invocation;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.config.ApplicationRuntimeStorageConfig;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.storage.fs.StorageFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
@Import({PlatformApiConfig.class, ApplicationRuntimeStorageConfig.class})
public class InvocationTestConfig {

  public static final String GLOBAL_ASYNC_CHANNEL = "global";

  public static final String SECOND_ASYNC_CHANNEL = "second";

  @Bean
  public TestApi testApi() {
    return new TestApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<TestApi> testApiProvider(TestApi testApi) {
    return ProviderApiInvocationHandler.providerOf(TestApi.class, TestApiImpl.NAME, testApi);
  }

  @Bean
  public TestPrimaryApi primaryApi() {
    return new TestPrimaryApiImpl(TestContributionApi.class);
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
  ObjectStorage objectStorage(ObjectApi objectApi) {
    return new StorageFS(TestFileUtil.testFsRootFolder(), objectApi);
  }

  @Bean
  public AsyncInvocationChannel globalChannel() {
    return new AsyncInvocationChannelImpl(GLOBAL_ASYNC_CHANNEL);
  }

  @Bean
  public AsyncInvocationChannel secondChannel() {
    return new AsyncInvocationChannelImpl(SECOND_ASYNC_CHANNEL);
  }

  @EventListener(ContextRefreshedEvent.class)
  public void clearFS(ContextRefreshedEvent event) throws Exception {
    TestFileUtil.clearTestDirectory();
    System.out.println("Test FS cleared...");
  }

}
