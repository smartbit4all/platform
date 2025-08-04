package org.smartbit4all.domain.data.storage;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sb4-storage")
public record StorageConfigurationProperties(
    Map<String, String> schemaAlias,
    StorageConfigurationProperties.FileSystem fs) {

  public record FileSystem(int parallelLoadThreshold) {
  }

}
