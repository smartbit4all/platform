package org.smartbit4all.storage.fs;

import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.config.ApplicationRuntimeStorageConfig;
import org.smartbit4all.domain.data.storage.Storage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({StorageTestConfig.class, ApplicationRuntimeStorageConfig.class})
@EnableTransactionManagement
@PropertySource("classpath:storagefs-cache.properties")
public class StorageFSTestConfig {

  @Bean
  public StorageFS defaultStorage(ObjectDefinitionApi objectApi) {
    return new StorageFS(
        TestFileUtil.testFsRootFolder(),
        objectApi);

    // new File("z:/test-fs")
  }

  @Bean(Storage.STORAGETX)
  public StorageTransactionManagerFS transactionManager(StorageFS storageFS) {
    return new StorageTransactionManagerFS(storageFS);
  }

}
