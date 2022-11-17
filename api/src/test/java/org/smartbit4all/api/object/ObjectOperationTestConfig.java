package org.smartbit4all.api.object;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.GroupsOfUser;
import org.smartbit4all.api.org.bean.User;
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
public class ObjectOperationTestConfig {

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
        .ref(GroupsOfUser.class, GroupsOfUser.USER_URI, User.class,
            ReferencePropertyKind.REFERENCE, AggregationKind.COMPOSITE)
        .ref(GroupsOfUser.class, GroupsOfUser.GROUPS, Group.class,
            ReferencePropertyKind.LIST, AggregationKind.COMPOSITE);
    // .ref(GroupsOfUser.class, "primaryGroup", Group.class, true);
  }

}
