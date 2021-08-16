package org.smartbit4all.api.invocation;

import org.smartbit4all.api.config.ApiConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ApiConfig.class})
public class InvocationTestConfig {


  @Bean
  public TestApi testAPi() {
    return new TestApiImpl();
  }

}
