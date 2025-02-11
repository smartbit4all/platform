package org.smartbit4all.api.object;

import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class ApplyChangeTestConfig extends ApplyChangeTestConfigBase {

  @Bean(Storage.STORAGETX)
  public StorageTransactionManagerFS transactionManager(
      @Autowired(required = false) StorageFS storageFS) {
    return new StorageTransactionManagerFS(storageFS);
  }

  @Bean
  public StorageFS defaultStorage(ObjectDefinitionApi objectApi) {
    return new StorageFS(
        TestFileUtil.testFsRootFolder(),
        objectApi);
  }

  @EventListener(ContextRefreshedEvent.class)
  public void clearFS(ContextRefreshedEvent event) throws Exception {
    TestFileUtil.clearTestDirectory();
    System.out.println("Test FS cleared...");
  }

}
