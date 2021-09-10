package org.smartbit4all.api.config;

import java.util.List;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationApiImpl;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.InvocationExecutionApiLocal;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.registration.ApiRegisterImpl;
import org.smartbit4all.api.invocation.registration.LocalApiInstantiator;
import org.smartbit4all.api.invocation.registration.ProtocolSpecificApiInstantiator;
import org.smartbit4all.domain.config.DomainConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The smartbit4all platform api config.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({DomainConfig.class})
public class PlatformApiConfig {

  @Bean
  public InvocationApi invocationApi() {
    return new InvocationApiImpl();
  }

  @Bean
  public InvocationExecutionApi invocationExcutionApiLocal() {
    return new InvocationExecutionApiLocal();
  }
  
  @Bean
  public ApiRegister apiRegister(
      List<ProtocolSpecificApiInstantiator> protocolSpecifigApiInstantiators) {
    return new ApiRegisterImpl(protocolSpecifigApiInstantiators);
  }
  
  @Bean
  public LocalApiInstantiator localApiInstantiator() {
    return new LocalApiInstantiator();
  }
  
}
