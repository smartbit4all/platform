package org.smartbit4all.api.invocation.restclient;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.config.InvocationApiMdmConfig;
import org.smartbit4all.core.io.TestFSCleaner;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.config.ApplicationRuntimeStorageConfig;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Import({
    PlatformApiConfig.class,
    InvocationRestClientConfig.class,
    ApplicationRuntimeStorageConfig.class,
    InvocationApiMdmConfig.class
})
public class DynamicRestCallerTestConfig {

  @Bean
  ObjectStorage objectStorage(ObjectDefinitionApi objectApi) {
    return new StorageFS(TestFileUtil.testFsRootFolder(), objectApi);
  }

  @Bean
  TestFSCleaner TestFSCleaner() {
    return new TestFSCleaner();
  }


  @Bean
  @ConditionalOnMissingBean
  PlatformTransactionManager platformTransactionManager() {
    return new StorageTransactionManagerFS();
  }

}
