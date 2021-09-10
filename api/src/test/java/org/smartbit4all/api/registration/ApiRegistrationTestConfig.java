package org.smartbit4all.api.registration;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.ApiPlaceholder;
import org.smartbit4all.api.invocation.TestContributionApi;
import org.smartbit4all.api.invocation.TestContributionApiImpl;
import org.smartbit4all.api.invocation.TestPrimaryApi;
import org.smartbit4all.api.invocation.TestPrimaryApiImpl;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(
    PlatformApiConfig.class
    )
public class ApiRegistrationTestConfig {

  @Bean
  public TestPrimaryApi primaryApi() {
    return new TestPrimaryApiImpl(TestPrimaryApi.class, TestContributionApi.class);
  }
  
  @Bean
  public TestContributionApi contributionApi2() {
    return new TestContributionApiImpl("contributionApi-ConfigTime");
  }
  
  @Bean
  public TestInterfaceToRegister testInterface(ApiRegister apiRegister) {
    return ApiPlaceholder.create(TestInterfaceToRegister.class, apiRegister);
  }
  
  public static interface TestInterfaceToRegister {
    
    String doSomething(String p1);
    
  }
  
}
