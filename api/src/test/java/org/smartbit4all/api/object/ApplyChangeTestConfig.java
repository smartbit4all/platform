package org.smartbit4all.api.object;

import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleAttachement;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.api.sample.bean.SampleLinkObject;
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

  @Bean
  public ObjectReferenceConfigs refDefs() {
    return new ObjectReferenceConfigs()
        .ref(SampleCategory.class,
            SampleCategory.SUB_CATEGORIES,
            SampleCategory.class,
            ReferencePropertyKind.LIST)
        .ref(SampleCategory.class,
            SampleCategory.CONTAINER_ITEMS,
            SampleContainerItem.class,
            ReferencePropertyKind.LIST)
        .ref(SampleCategory.class,
            SHADOW_ITEMS,
            SampleContainerItem.class,
            ReferencePropertyKind.LIST)
        .ref(SampleCategory.class,
            SampleCategory.LINKS,
            SampleLinkObject.class,
            ReferencePropertyKind.LIST,
            AggregationKind.INLINE)
        .ref(SampleLinkObject.class,
            SampleLinkObject.CATEGORY,
            SampleCategory.class,
            ReferencePropertyKind.REFERENCE)
        .ref(SampleLinkObject.class,
            SampleLinkObject.ITEM,
            SampleContainerItem.class,
            ReferencePropertyKind.REFERENCE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.USER_URI,
            User.class,
            ReferencePropertyKind.REFERENCE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.ATTACHMENTS,
            SampleAttachement.class,
            ReferencePropertyKind.LIST,
            AggregationKind.COMPOSITE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.MAIN_DOCUMENT,
            SampleAttachement.class,
            ReferencePropertyKind.REFERENCE,
            AggregationKind.COMPOSITE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.DATASHEET,
            SampleDataSheet.class,
            ReferencePropertyKind.REFERENCE,
            AggregationKind.COMPOSITE)
        .ref(SampleAttachement.class,
            SampleAttachement.CONTENT,
            BinaryContent.class,
            ReferencePropertyKind.REFERENCE);
  }

}
