package org.smartbit4all.storage.fs;

import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({StorageTestConfig.class})
@EnableTransactionManagement
public class StorageInMemoryTestConfig {

  @Bean
  public ObjectStorage defaultStorage(ObjectDefinitionApi objectApi) {
    return new ObjectStorageInMemory(objectApi);
  }

}
