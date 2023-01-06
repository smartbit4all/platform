package org.smartbit4all.api.collection;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.config.ApplicationRuntimeStorageConfig;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({PlatformApiConfig.class, ApplicationRuntimeStorageConfig.class})
@EnableTransactionManagement
public class CollectionTestConfig {

  public static final String SHADOW_ITEMS = "shadowItems";

  @Bean
  public StorageFS defaultStorage(ObjectApi objectApi) {
    return new StorageFS(
        TestFileUtil.testFsRootFolder(),
        objectApi);
  }

  @Bean(Storage.STORAGETX)
  public StorageTransactionManagerFS transactionManager(StorageFS storageFS) {
    return new StorageTransactionManagerFS(storageFS);
  }

  @EventListener(ContextRefreshedEvent.class)
  public void clearFS(ContextRefreshedEvent event) throws Exception {
    TestFileUtil.clearTestDirectory();
    System.out.println("Test FS cleared...");
  }

  @Bean
  public SearchIndex<TestFilter, SampleDataSheet> sampleDatasheetIndex() {
    return new SearchIndexWithFilterBean<>(CollectionApiTest.SCHEMA, CollectionApiTest.MY_SEARCH,
        TestFilter.class, CollectionApiTest.SCHEMA, SampleDataSheet.class)
            .map(TestFilter.NAME, SampleDataSheet.NAME).map(TestFilter.URI, SampleDataSheet.URI)
            .map(TestFilter.ISODD, n -> Boolean.valueOf("odd".equals(n)), SampleDataSheet.NAME)
            .mapComplex(TestFilter.CAPTION,
                on -> {
                  return on.getValueAsString(SampleDataSheet.NAME)
                      + StringConstant.DOT
                      + on.getValueAsString(SampleDataSheet.NAME);
                });
  }

}
