package org.smartbit4all.api.org;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionApiTestImpl;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class})
public class OrgApiTestConfig {

  @Bean
  public SessionApi userSessionApi() {
    return new SessionApiTestImpl();
  }

  @Bean
  public MyModuleSecurityOption myModuleSecurityOption() {
    return new MyModuleSecurityOption();
  }

  @Bean
  public OrgApiStorageImpl orgApi() {
    return new OrgApiStorageImpl();
  }

  @Bean
  ObjectStorageInMemory createInMemoryStorage(ObjectDefinitionApi objectApi) {
    return new ObjectStorageInMemory(objectApi);
  }

}
