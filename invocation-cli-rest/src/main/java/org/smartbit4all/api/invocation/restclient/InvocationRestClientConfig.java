package org.smartbit4all.api.invocation.restclient;

import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class InvocationRestClientConfig {

  @Bean
  @ConditionalOnMissingBean(RestTemplate.class)
  public RestTemplate restTemplate() {
    // TODO a more complex rest template instantiation could be used here with RestTemplateBuilder
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
  }

  @Bean
  @ConditionalOnMissingBean(ObjectMapper.class)
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper;
  }

  @Bean
  public InvocationExecutionApi remoteExecutionApiRestclient() {
    return new InvocationExecutionApiRestclient();
  }


}
