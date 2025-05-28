package org.smartbit4all.api.mdm.restserver.config;

import org.smartbit4all.api.mdm.restserver.MasterDataManagementApiController;
import org.smartbit4all.api.mdm.restserver.MasterDataManagementApiDelegate;
import org.smartbit4all.api.mdm.restserver.MasterDataManagementApiDelegateImpl;
import org.smartbit4all.api.restserver.PlatformApiCommonSrvRestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiCommonSrvRestConfig.class})
public class MasterDataManagementApiSrvRestConfig {

  @Bean
  public MasterDataManagementApiDelegate masterDataManagementApiDelegate() {
    return new MasterDataManagementApiDelegateImpl();
  }

  @Bean
  public MasterDataManagementApiController masterDataManagementApiController(
      MasterDataManagementApiDelegate api) {
    return new MasterDataManagementApiController(api);
  }

}
