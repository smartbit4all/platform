package org.smartbit4all.api.invocation;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.domain.config.ApplicationRuntimeStorageConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({PlatformApiConfig.class, ApplicationRuntimeStorageConfig.class})
@EnableTransactionManagement
public class InvocationTestConfig {

  public static final String GLOBAL_ASYNC_CHANNEL = "global";

  public static final String SECOND_ASYNC_CHANNEL = "second";

  public static final String THIRD_ASYNC_CHANNEL = "third";

  public static final String USER1 = "USER1";

  public static final String USER2 = "USER2";

  public static final String USER3 = "USER3";

  @Bean
  public TestApi testApi() {
    return new TestApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<TestApi> testApiProvider(TestApi testApi) {
    return ProviderApiInvocationHandler.providerOf(TestApi.class, TestApiImpl.NAME, testApi);
  }

  @Bean
  public TestRunHelperApi testRunHelperApi() {
    return new TestRunHelperApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<TestRunHelperApi> testRunHelperApiProvider(
      TestRunHelperApi testRunHelperApi) {
    return ProviderApiInvocationHandler.providerOf(TestRunHelperApi.class, testRunHelperApi);
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
  public AsyncInvocationChannel globalChannel() {
    return new AsyncInvocationChannelImpl(GLOBAL_ASYNC_CHANNEL);
  }

  @Bean
  public AsyncInvocationChannel secondChannel() {
    return new AsyncInvocationChannelImpl(SECOND_ASYNC_CHANNEL);
  }

  @Bean
  public AsyncInvocationChannel thirdChannel() {
    return new AsyncInvocationChannelImpl(THIRD_ASYNC_CHANNEL)
        .technicalUserName(USER1);
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
