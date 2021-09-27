package org.smartbit4all.api.invocation.restserver;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.ApiPlaceholder;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.restserver.config.InvocationSrvRestConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({
    PlatformApiConfig.class,
    InvocationSrvRestConfig.class
})
public class InvocationApiRestServerTestConfig {

  @Bean
  @ConditionalOnMissingBean(RestTemplate.class)
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
  }

  @Bean
  public TestApiInterface testApiInterface(ApiRegister apiRegister) {
    return ApiPlaceholder.create(TestApiInterface.class, apiRegister);
  }

  public interface TestApiInterface {
    String doSomething(String param);
  }
}
