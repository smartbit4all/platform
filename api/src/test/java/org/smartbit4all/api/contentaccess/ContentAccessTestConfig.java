package org.smartbit4all.api.contentaccess;

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
import org.smartbit4all.api.objectshare.ObjectShareApi;
import org.smartbit4all.api.objectshare.ObjectShareApiInMemoryImpl;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import(PlatformApiConfig.class)
public class ContentAccessTestConfig {

  @Bean
  ContentAccessApi contentAccessApi(
      ObjectShareApi objectShareApi,
      StorageApi objectStorage,
      BinaryContentApi binaryContentApi) {

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
  ObjectStorageInMemory createInMemoryStorage(ObjectDefinitionApi objectApi) {
    return new ObjectStorageInMemory(objectApi);
  }

  static File getBinaryDataApiRootFolder() {
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
