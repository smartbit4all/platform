package org.smartbit4all.api.invocation.restclient;

import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.restclient.apiregister.InvocationExecutionApiRestGen;
import org.smartbit4all.api.invocation.restclient.apiregister.RestGenApiInstantiator;
import org.smartbit4all.api.invocation.restclient.apiregister.RestGeneratedApiRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
  public InvocationRestSerializer invocationRestSerializer(ObjectMapper objectMapper) {
    return new InvocationRestSerializer(objectMapper);
  }
  
  @Bean
  @ConditionalOnMissingBean(ObjectMapper.class)
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper;
  }
  
  @Bean
  public RestGenApiInstantiator restApiInstantiator(InvocationApi invocationApi) {
    return new RestGenApiInstantiator(invocationApi);
  }
  
  @Bean
  @ConditionalOnBean(RestGeneratedApiRegistry.class)
  public InvocationExecutionApiRestGen invocationExecutionApiRestGen(
      RestGeneratedApiRegistry restGenApiRegistry) {
    return new InvocationExecutionApiRestGen(restGenApiRegistry);
  }
  
  
}
