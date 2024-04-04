package org.smartbit4all.api.wellknow.restserver.config;

import org.smartbit4all.api.restserver.PlatformApiCommonSrvRestConfig;
import org.smartbit4all.api.wellknow.restserver.impl.WellknownDelegateImpl;
import org.smartbit4all.api.wellknown.restserver.WellknownApiController;
import org.smartbit4all.api.wellknown.restserver.WellknownApiDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiCommonSrvRestConfig.class})
public class WellknownSrvRestConfig {

  @Bean
  public WellknownApiDelegate wellknownApiDelegate() {
    return new WellknownDelegateImpl();
  }

  @Bean
  public WellknownApiController wellknownApiController(WellknownApiDelegate delegate) {
    return new WellknownApiController(delegate);
  }
}
