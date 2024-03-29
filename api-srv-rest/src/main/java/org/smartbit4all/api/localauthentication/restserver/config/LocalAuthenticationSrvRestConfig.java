package org.smartbit4all.api.localauthentication.restserver.config;

import org.smartbit4all.api.localauthentication.restserver.LocalAuthenticationApiController;
import org.smartbit4all.api.localauthentication.restserver.LocalAuthenticationApiDelegate;
import org.smartbit4all.api.localauthentication.restserver.impl.LocalAuthenticationApiDelegateImpl;
import org.smartbit4all.api.restserver.PlatformApiCommonSrvRestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiCommonSrvRestConfig.class})
public class LocalAuthenticationSrvRestConfig {

  @Bean
  public LocalAuthenticationApiDelegate localAuthenticationApiDelegate() {
    return new LocalAuthenticationApiDelegateImpl();
  }

  @Bean
  public LocalAuthenticationApiController localAuthenticationApiController(
      LocalAuthenticationApiDelegate delegate) {
    return new LocalAuthenticationApiController(delegate);
  }

}
