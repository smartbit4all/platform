package org.smartbit4all.api.applychange;

import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleAttachement;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectReferenceConfigs;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({PlatformApiConfig.class})
@EnableTransactionManagement
public class ApplyChangeTestConfig {

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

  @Bean
  public ObjectReferenceConfigs refDefs() {
    return new ObjectReferenceConfigs().ref(SampleCategory.class.getName(),
        SampleCategory.SUB_CATEGORIES, SampleCategory.class.getName(), false)
        .ref(SampleCategory.class.getName(),
            SampleCategory.CONTAINER_ITEMS, SampleContainerItem.class.getName(), true)
        .ref(SampleContainerItem.class.getName(),
            SampleContainerItem.USER_URI, User.class.getName(), false)
        .ref(SampleContainerItem.class.getName(),
            SampleContainerItem.ATTACHMENTS, SampleAttachement.class.getName(), true)
        .ref(SampleContainerItem.class.getName(),
            SampleContainerItem.MAIN_DOCUMENT, SampleAttachement.class.getName(), true)
        .ref(SampleContainerItem.class.getName(),
            SampleContainerItem.DATASHEET, SampleAttachement.class.getName(), true)
        .ref(SampleAttachement.class.getName(),
            SampleAttachement.CONTENT, BinaryContent.class.getName(), false);
  }

}
