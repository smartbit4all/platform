package org.smartbit4all.api.contentaccess.restserver.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryContentApi;
import org.smartbit4all.api.binarydata.BinaryContentApiImpl;
import org.smartbit4all.api.binarydata.BinaryDataApi;
import org.smartbit4all.api.binarydata.BinaryDataApiPrimary;
import org.smartbit4all.api.binarydata.fs.BinaryDataApiFS;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.contentaccess.ContentAccessApi;
import org.smartbit4all.api.contentaccess.ContentAccessApiImpl;
import org.smartbit4all.api.objectshare.ObjectShareApi;
import org.smartbit4all.api.objectshare.ObjectShareApiInMemoryImpl;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import({ContentAccessSrvRestConfig.class, PlatformApiConfig.class})
@EnableAutoConfiguration
public class ContentAccessSrvRestTestConfig {

  @Bean
  ContentAccessApi contentAccessApi(
      ObjectShareApi objectShareApi,
      StorageApi objectStorage,
      BinaryContentApi binaryContentApi) throws Exception {

    return new ContentAccessApiImpl(objectShareApi, objectStorage, binaryContentApi);
  }

  @Bean
  ObjectShareApi objectShareApi() {
    return new ObjectShareApiInMemoryImpl();
  }

  @Bean
  BinaryContentApi binaryContentApi(BinaryDataApi binaryDataApi) {
    return new BinaryContentApiImpl(binaryDataApi);
  }

  @Bean
  @Primary
  BinaryDataApi binaryDataApiPrimary() {
    return new BinaryDataApiPrimary();
  }

  @Bean
  List<BinaryDataApi> binaryDataApi() {
    return Arrays
        .asList(new BinaryDataApiFS(ContentAccessApi.SCHEME, getBinaryDataApiRootFolder()));
  }

  @Bean
  ObjectStorageInMemory createInMemoryStorage(ObjectApi objectApi) {
    return new ObjectStorageInMemory(objectApi);
  }

  public static File getBinaryDataApiRootFolder() {
    File file = new File("./src/test/resources/contentAccessData");
    if (!file.exists()) {
      try {
        Files.createDirectory(file.toPath());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return file;
  }
}
