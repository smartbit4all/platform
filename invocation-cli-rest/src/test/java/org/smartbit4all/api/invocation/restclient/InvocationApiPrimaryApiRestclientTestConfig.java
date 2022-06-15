package org.smartbit4all.api.invocation.restclient;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.TestContributionApi;
import org.smartbit4all.api.invocation.TestContributionApiImpl;
import org.smartbit4all.api.invocation.TestPrimaryApi;
import org.smartbit4all.api.invocation.TestPrimaryApiImpl;
import org.smartbit4all.core.io.TestFSCleaner;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.config.ApplicationRuntimeStorageConfig;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.storage.fs.StorageFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    PlatformApiConfig.class,
    InvocationRestClientConfig.class,
    ApplicationRuntimeStorageConfig.class
})
public class InvocationApiPrimaryApiRestclientTestConfig {

  @Bean
  ObjectStorage objectStorage(ObjectApi objectApi) {
    return new StorageFS(TestFileUtil.testFsRootFolder(), objectApi);
  }

  @Bean
  TestFSCleaner TestFSCleaner() {
    return new TestFSCleaner();
  }

  @Bean
  TestPrimaryApi testPrimaryApi() {
    return new TestPrimaryApiImpl(TestContributionApi.class);
  }

  @Bean
  TestContributionApi testContributionApi() {
    return new TestContributionApiImpl(
        TestContributionApiImpl.NAME);
  }
}
