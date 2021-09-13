package org.smartbit4all.api.binarydata.config;

import org.smartbit4all.api.binarydata.BinaryContentApi;
import org.smartbit4all.api.binarydata.BinaryContentApiImpl;
import org.smartbit4all.api.binarydata.BinaryDataApi;
import org.smartbit4all.api.binarydata.BinaryDataApiPrimary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BinaryDataConfig {
  
  @Bean
  @Primary
  public BinaryDataApi binaryDataApiPrimary() {
    return new BinaryDataApiPrimary();
  }

  @Bean
  public BinaryContentApi binaryContentApi(BinaryDataApi binaryDataApi) {
    return new BinaryContentApiImpl(binaryDataApi);
  }

}
