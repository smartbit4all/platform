package org.smartbit4all.api.binarydata;

import org.smartbit4all.api.binarydata.config.BinaryDataConfig;
import org.smartbit4all.api.binarydata.fs.BinaryDataApiFS;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.io.TestFileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class, BinaryDataConfig.class})
public class BinaryContentTestConfig {

  @Bean
  public BinaryDataApi binaryDataApi() {
    return new BinaryDataApiFS(BinaryContentTest.BINARYDATA_SCHEMA,
        TestFileUtil.testFsRootFolder());
  }

}
