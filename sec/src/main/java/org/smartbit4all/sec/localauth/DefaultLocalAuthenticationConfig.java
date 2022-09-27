package org.smartbit4all.sec.localauth;

import org.smartbit4all.sec.authentication.AuthenticationDataProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DefaultLocalAuthenticationConfig {

  @Bean
  public LocalAuthenticationProvider localAuthenticationProvider() {
    return new LocalAuthenticationProvider();
  }

  @Bean
  public SessionLocalAuthenticationProvider sessionLocalAuthenticationProvider() {
    return new SessionLocalAuthenticationProvider(localAuthenticationProvider());
  }

  @Bean
  public LocalAuthenticationService localAuthenticationService() {
    return new LocalAuthenticationServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationDataProvider localAuthenticationDataProvider() {
    return new LocalAuthenticationDataProvider();
  }

}
