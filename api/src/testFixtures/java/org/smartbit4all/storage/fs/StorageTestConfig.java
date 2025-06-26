package org.smartbit4all.storage.fs;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.AsyncInvocationChannel;
import org.smartbit4all.api.invocation.AsyncInvocationChannelImpl;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({PlatformApiConfig.class})
@EnableTransactionManagement
public class StorageTestConfig {

  public static final String TESTSCHEME = "testscheme";

  public static final String TESTSCHEMESINGLE = "testschemesingle";

  public static final String GLOBAL_ASYNC_CHANNEL = "global";

  @Bean
  public Storage testStorageScheme(ObjectDefinitionApi objectDefinitionApi,
      ObjectStorage objectStorage) {
    return new Storage(TESTSCHEME, objectDefinitionApi, objectStorage);
  }

  @Bean
  public Storage testStorageSchemeSingle(ObjectDefinitionApi objectDefinitionApi,
      ObjectStorage objectStorage) {
    Storage storage =
        new Storage(TESTSCHEMESINGLE, objectDefinitionApi, objectStorage,
            VersionPolicy.SINGLEVERSION);
    return storage;
  }

  @Bean
  public StorageTestApi storageTestApi() {
    return new StorageTestApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<StorageTestApi> storageTestPageApiProvider(
      StorageTestApi api) {
    return Invocations.asProvider(StorageTestApi.class, api);
  }

  @Bean
  public AsyncInvocationChannel globalChannel() {
    return new AsyncInvocationChannelImpl(GLOBAL_ASYNC_CHANNEL);
  }


}
