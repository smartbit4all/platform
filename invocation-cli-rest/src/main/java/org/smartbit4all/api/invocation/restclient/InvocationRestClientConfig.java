package org.smartbit4all.api.invocation.restclient;

import java.util.concurrent.TimeUnit;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Configuration
@EnableConfigurationProperties(DynamicRestRequestCacheProperties.class)
public class InvocationRestClientConfig {

  @Bean
  @ConditionalOnMissingBean(RestTemplate.class)
  public RestTemplate restTemplate(@Autowired(
      required = false) RestTemplateBuilder restTemplateBuilder) {
    // TODO a more complex rest template instantiation could be used here with RestTemplateBuilder
    if (restTemplateBuilder != null) {
      return restTemplateBuilder.build();
    }
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

  @Bean
  public DynamicRestCallerApi dynamicRestCallerApi() {
    return new DynamicRestCallerApiImpl();
  }

  @Bean
  ProviderApiInvocationHandler<DynamicRestCallerApi> dynamicRestCallerApiProvider(
      DynamicRestCallerApi api) {
    return Invocations.asProvider(
        DynamicRestCallerApi.class,
        api);
  }

  @Bean
  public Cache<String, ResponseEntity<Object>> dynamicRestRequestCache(
      DynamicRestRequestCacheProperties props) {

    if (!props.isEnabled()) {
      return CacheBuilder.newBuilder()
          .maximumSize(0)
          .build();
    }

    return CacheBuilder.newBuilder()
        .expireAfterWrite(props.getTtlMinutes(), TimeUnit.MINUTES)
        .build();
  }

}
