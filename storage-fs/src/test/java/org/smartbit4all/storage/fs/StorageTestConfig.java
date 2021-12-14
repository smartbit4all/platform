package org.smartbit4all.storage.fs;

import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.config.DomainConfig;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.Storage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({DomainConfig.class})
@EnableTransactionManagement
public class StorageTestConfig {

  public static final String TESTSCHEME = "testscheme";

  @Bean
  public Storage testStorageScheme(ObjectApi objectApi, ObjectStorage objectStorage) {
    return new Storage(TESTSCHEME, objectApi, objectStorage);
  }

  @Bean
  public StorageTestApi storageFSTestApi() {
    return new StorageTestApiImpl();
  }

}