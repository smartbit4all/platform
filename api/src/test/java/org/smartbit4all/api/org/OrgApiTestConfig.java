package org.smartbit4all.api.org;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.api.session.UserSessionApiLocal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class})
public class OrgApiTestConfig{
  
  @Bean
  public UserSessionApi userSessionApi() {
    return new UserSessionApiLocal();
  }
  
  @Bean
  public MyModuleSecurityOption myModuleSecurityOption(UserSessionApi userSessionApi) {
    return new MyModuleSecurityOption(userSessionApi);
  }
  
  @Bean
  public OrgApiInMemory orgApiInMemory(MyModuleSecurityOption myModuleSecurityOption) {
    OrgApiInMemory orgApiInMemory = new OrgApiInMemory(null);
    orgApiInMemory.analyzeSecurityOptions(myModuleSecurityOption);
    return orgApiInMemory;
  }
}