package org.smartbit4all.api.documentation;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class})
public class DocumentationTestConfig {

  @Bean
  public ObjectStorage objectStorage(ObjectApi objectApi) {
    return new ObjectStorageInMemory(objectApi);
  }

}
