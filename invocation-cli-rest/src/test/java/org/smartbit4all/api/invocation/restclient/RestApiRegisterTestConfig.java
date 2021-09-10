package org.smartbit4all.api.invocation.restclient;

import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.restclient.apiregister.RestGenApiInstantiator;
import org.smartbit4all.api.invocation.restclient.apiregister.RestGeneratedApiRegistry;
import org.smartbit4all.api.invocation.restclient.apiregister.RestGeneratedApiRegistryImpl;
import org.smartbit4all.api.invocation.restclient.apiregister.RestclientFactoryUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestApiRegisterTestConfig {

  @Bean
  public RestGeneratedApiRegistry restGeneratedApiRegistry(ApiRegister apiRegister,
      RestGenApiInstantiator restGenApiInstantiator,
      RestTemplate restTemplate) {
    
    RestGeneratedApiRegistryImpl restGenRegistry =
        new RestGeneratedApiRegistryImpl(apiRegister, restGenApiInstantiator);
    
    restGenRegistry.addRestStubFactory(TestRestclient.class, apiInfo -> {
      return RestclientFactoryUtil.createConfiguredApi(apiInfo, restTemplate,
          TestRestclientImpl::new);
    });
    return restGenRegistry;
  }
}
