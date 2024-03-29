package org.smartbit4all.storage.fs;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({PlatformApiConfig.class})
@EnableTransactionManagement
public class StorageTestConfig {

  public static final String TESTSCHEME = "testscheme";

  public static final String TESTSCHEMESINGLE = "testschemesingle";

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
  public StorageTestApi storageFSTestApi() {
    return new StorageTestApiImpl();
  }

  @EventListener(ContextRefreshedEvent.class)
  public void clearFS(ContextRefreshedEvent event) throws Exception {
    TestFileUtil.clearTestDirectory();
    System.out.println("Test FS cleared...");
  }


}
