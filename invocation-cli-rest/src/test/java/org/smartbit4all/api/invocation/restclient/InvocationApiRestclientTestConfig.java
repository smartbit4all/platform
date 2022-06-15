package org.smartbit4all.api.invocation.restclient;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.ApiInvocationHandler;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.TestApi;
import org.smartbit4all.api.invocation.TestApiImpl;
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
public class InvocationApiRestclientTestConfig {

  @Bean
  ObjectStorage objectStorage(ObjectApi objectApi) {
    return new StorageFS(TestFileUtil.testFsRootFolder(), objectApi);
  }

  @Bean
  TestFSCleaner TestFSCleaner() {
    return new TestFSCleaner();
  }

  @Bean
  TestApi testApi(InvocationApi invocationApi) {
    return ApiInvocationHandler.createProxy(TestApi.class, TestApiImpl.NAME, invocationApi);
  }


}
