package org.smartbit4all.api.kerberosauthentication.restserver;

import org.smartbit4all.api.restserver.PlatformApiCommonSrvRestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiCommonSrvRestConfig.class})
public class KerberosAuthenticationSrvRestConfig {

  @Bean
  public KerberosAuthenticationApiDelegate kerberosAuthenticationApiDelegate() {
    return new KerberosAuthenticationApiDelegateImpl();
  }

  @Bean
  public KerberosAuthenticationApiController kerberosAuthenticationApiController(
      KerberosAuthenticationApiDelegate delegate) {
    return new KerberosAuthenticationApiController(delegate);
  }

}
