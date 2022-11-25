package org.smartbit4all.sec.config;

import org.smartbit4all.api.org.OrgApiStorageImpl;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.sec.jwt.JwtUtil;
import org.smartbit4all.sec.localauth.DefaultLocalAuthenticationConfig;
import org.smartbit4all.sec.localauth.SessionLocalAuthenticationProvider;
import org.smartbit4all.sec.session.SessionApiImpl;
import org.smartbit4all.sec.session.SessionManagementApiImpl;
import org.smartbit4all.sec.token.SessionTokenHandler;
import org.smartbit4all.sec.token.SessionTokenHandlerJWT;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class SecurityLocalTestConfig extends DefaultLocalAuthenticationConfig {

  @Bean
  public AuthenticationManager authenticationManager(SessionLocalAuthenticationProvider p) {
    return new ProviderManager(p);
  }

  @Bean
  public OrgApiStorageImpl orgApi() {
    return new OrgApiStorageImpl();
  }

  @Bean
  public SessionManagementApi sessionManagementApi() {
    SessionManagementApiImpl sessionApi = new SessionManagementApiImpl();
    sessionApi.setDefaultLocaleProvider(() -> "hu");
    return sessionApi;
  }

  @Bean
  public SessionApi sessionApi() {
    return new SessionApiImpl();
  }

  @Bean
  public SessionTokenHandler sessionTokenHandler() {
    return new SessionTokenHandlerJWT();
  }

  @Bean
  public JwtUtil jwtUtil() {
    return new JwtUtil();
  }

  @Bean
  @ConditionalOnMissingBean(ObjectMapper.class)
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper;
  }

}
