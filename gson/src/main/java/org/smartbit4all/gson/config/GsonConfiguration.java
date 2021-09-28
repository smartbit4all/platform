package org.smartbit4all.gson.config;

import org.smartbit4all.gson.GsonObjectSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfiguration {

  @Bean
  public GsonObjectSerializer gesonGsonObjectSerializer() {
    return new GsonObjectSerializer();
  }

}
