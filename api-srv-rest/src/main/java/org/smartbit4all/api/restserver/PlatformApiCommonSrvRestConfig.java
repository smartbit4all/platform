package org.smartbit4all.api.restserver;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * The smartbit4all platform api config.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({PlatformApiConfig.class})
public class PlatformApiCommonSrvRestConfig {

  @Bean
  @ConditionalOnMissingBean(ObjectMapper.class)
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }

}
