package org.smartbit4all.storage.fs;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.object.ObjectApi;
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

  @Bean
  public Storage testStorageScheme(ObjectApi objectApi, ObjectStorage objectStorage) {
    return new Storage(TESTSCHEME, objectApi, objectStorage);
  }

  @Bean
  public Storage testStorageSchemeSingle(ObjectApi objectApi, ObjectStorage objectStorage) {
    Storage storage =
        new Storage(TESTSCHEMESINGLE, objectApi, objectStorage, VersionPolicy.SINGLEVERSION);
    return storage;
  }

  @Bean
  public StorageTestApi storageFSTestApi() {
    return new StorageTestApiImpl();
  }

}
