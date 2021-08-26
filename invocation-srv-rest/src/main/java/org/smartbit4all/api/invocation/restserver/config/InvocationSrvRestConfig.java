package org.smartbit4all.api.invocation.restserver.config;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.restserver.InvocationApiDelegate;
import org.smartbit4all.api.invocation.restserver.impl.InvocationApiDelegateImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The smartbit4all platform api config.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({PlatformApiConfig.class})
public class InvocationSrvRestConfig {

  @Bean
  public InvocationApiDelegate invocationApiDelegate() {
    return new InvocationApiDelegateImpl();
  }

}
