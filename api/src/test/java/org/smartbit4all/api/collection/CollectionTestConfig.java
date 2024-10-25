package org.smartbit4all.api.collection;

import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({CollectionTestBaseConfig.class,
    TestFSConfig.class})
@EnableTransactionManagement
public class CollectionTestConfig {

  @Bean(Storage.STORAGETX)
  public StorageTransactionManagerFS transactionManager(StorageFS storageFS) {
    return new StorageTransactionManagerFS(storageFS);
  }


}
