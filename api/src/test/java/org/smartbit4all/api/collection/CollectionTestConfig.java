package org.smartbit4all.api.collection;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({PlatformApiConfig.class})
@EnableTransactionManagement
public class CollectionTestConfig {

  public static final String SHADOW_ITEMS = "shadowItems";

  @Bean
  public StorageFS defaultStorage(ObjectApi objectApi) {
    return new StorageFS(
        TestFileUtil.testFsRootFolder(),
        objectApi);
  }

  @Bean(Storage.STORAGETX)
  public StorageTransactionManagerFS transactionManager(StorageFS storageFS) {
    return new StorageTransactionManagerFS(storageFS);
  }

}
