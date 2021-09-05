package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;
import org.smartbit4all.api.invocation.bean.TestDataBean;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.smartbit4all.domain.data.storage.ObjectUriProvider;
import org.smartbit4all.domain.data.storage.Storage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class})
public class InvocationTestConfig {


  private final class ObjectUriProviderImplementation<T> implements ObjectUriProvider<T> {
    private final String path;

    public ObjectUriProviderImplementation(String path) {
      super();
      this.path = path;
    }

    @Override
    public URI constructUri(T object) {
      return URI.create(
          "invocation:" + path + "#"
              + sequence.getAndIncrement());
    }
  }

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

  public static final AtomicLong sequence = new AtomicLong();

  @Bean
  Storage<InvocationRequestTemplate> invocationRequestTemplateStorage() {
    Storage<InvocationRequestTemplate> memoryStorage = createInMemoryStorage(null,
        InvocationRequestTemplate.class,
        new ObjectUriProviderImplementation<InvocationRequestTemplate>(
            "/org/smartbit4all/api/invocation/bean/InvocationRequestTemplate"));
    return memoryStorage;
  }

  @Bean
  Storage<TestDataBean> testDataBeanStorage() {
    Storage<TestDataBean> memoryStorage = createInMemoryStorage((TestDataBean t) -> t.getUri(),
        TestDataBean.class,
        new ObjectUriProviderImplementation<TestDataBean>(
            "/org/smartbit4all/api/invocation/bean/TestDataBean"));
    return memoryStorage;
  }

  private <T> Storage<T> createInMemoryStorage(Function<T, URI> uriAccessor, Class<T> clazz,
      ObjectUriProvider<T> uriProvider) {
    ObjectStorageInMemory<T> objectStorageInMemory =
        new ObjectStorageInMemory<T>(uriAccessor, null);
    objectStorageInMemory.setUriProvider(uriProvider);
    return new Storage<>(clazz, objectStorageInMemory, Collections.emptyList());
  }

}
