package org.smartbit4all.api.invocation;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.config.ApplicationRuntimeStorageConfig;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({PlatformApiConfig.class, ApplicationRuntimeStorageConfig.class})
@EnableTransactionManagement
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
  ObjectStorage objectStorage(ObjectDefinitionApi objectApi) {
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

  @Bean(Storage.STORAGETX)
  public StorageTransactionManagerFS transactionManager(StorageFS storageFS) {
    return new StorageTransactionManagerFS(storageFS);
  }

  @Bean
  public TestEventPublisherApi testEventPublisherApi() {
    return new TestEventPublisherApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<TestEventPublisherApi> testEventPublisherApiProvider(
      TestEventPublisherApi api) {
    return ProviderApiInvocationHandler.providerOf(TestEventPublisherApi.class, api);
  }

  @Bean
  public TestEventSubscriberApi testEventSubscriberApi() {
    return new TestEventSubscriberApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<TestEventSubscriberApi> testEventSubscriberApiProvider(
      TestEventSubscriberApi api) {
    return ProviderApiInvocationHandler.providerOf(TestEventSubscriberApi.class, api);
  }

}
