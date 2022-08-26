package org.smartbit4all.api.object;

import org.smartbit4all.api.config.PlatformApiConfig;
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
public class ObjectDeepCopyApiTestConfig {

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
    return new ObjectReferenceConfigs().ref(GroupsOfUser.class.getName(),
        GroupsOfUser.USER_URI, User.class.getName(), true)
        .ref(GroupsOfUser.class.getName(), GroupsOfUser.GROUPS, Group.class.getName(), true);
    // .ref(GroupsOfUser.class.getName(), "primaryGroup", Group.class.getName(), true);
  }

}
