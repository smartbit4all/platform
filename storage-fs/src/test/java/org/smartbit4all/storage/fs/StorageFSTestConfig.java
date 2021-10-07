package org.smartbit4all.storage.fs;

import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.config.DomainConfig;
import org.smartbit4all.domain.data.storage.Storage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DomainConfig.class})
public class StorageFSTestConfig {

  public static final String TESTSCHEME = "testscheme";

  @Bean
  public StorageFS defaultFSStorage(ObjectApi objectApi) {
    return new StorageFS(
        TestFileUtil.testFsRootFolder(),
        objectApi);
  }

  @Bean
  public Storage testStorageScheme(ObjectApi objectApi, StorageFS storageFS) {
    return new Storage(TESTSCHEME, objectApi, storageFS);
  }

}
