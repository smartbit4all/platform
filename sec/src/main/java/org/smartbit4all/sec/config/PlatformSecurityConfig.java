package org.smartbit4all.sec.config;

import org.smartbit4all.core.reactive.ObjectChangePublisher;
import org.smartbit4all.sec.session.ObjectChangePublisherSpringSecAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

@Configuration
public class PlatformSecurityConfig {

  @Bean
  @Primary
  @Scope("prototype")
  public ObjectChangePublisher<?> objectChangePublisher() {
    return new ObjectChangePublisherSpringSecAware<>();
  }

}
