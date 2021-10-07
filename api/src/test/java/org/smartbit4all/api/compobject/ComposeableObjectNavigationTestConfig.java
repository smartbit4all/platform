package org.smartbit4all.api.compobject;

import java.util.HashSet;
import java.util.Set;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PlatformApiConfig.class)
public class ComposeableObjectNavigationTestConfig {

  @Bean
  ObjectStorageInMemory createInMemoryStorage(ObjectApi objectApi) {
    return new ObjectStorageInMemory(objectApi);
  }

  @Bean
  ComposeableObjectPrimaryApi composeableObjectPrimary(StorageApi storageApi) {
    CompositeObjectApi compositeObjectApi =
        new CompositeObjectApi(storageApi.get(CompositeObjectApi.SCHEME));

    TestTreeObjectApi testTreeObjectApi =
        new TestTreeObjectApi(storageApi.get(TestTreeObjectApi.SCHEME));

    ComposeableObjectPrimaryApi composeablePrimary = new ComposeableObjectPrimaryApi();
    composeablePrimary.add(compositeObjectApi);
    composeablePrimary.add(testTreeObjectApi);

    Set<Class<?>> beans = new HashSet<>();
    beans.add(TestTreeObject.class);
    composeablePrimary.addDescriptor(ApiBeanDescriptor.of(beans));

    return composeablePrimary;
  }

  @Bean
  ComposeableObjectNavigation composeableNavigation(
      ComposeableObjectPrimaryApi compObjPrimaryApi,
      StorageApi storageApi) {

    return new ComposeableObjectNavigation(
        ComposeableObjectApi.SCHEME,
        compObjPrimaryApi,
        storageApi.get(ComposeableObjectApi.SCHEME));
  }

}
