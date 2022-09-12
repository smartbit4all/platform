package org.smartbit4all.api.view.restserver.config;

import org.smartbit4all.api.restserver.PlatformApiCommonSrvRestConfig;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.ViewApiImpl;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.ViewContextServiceImpl;
import org.smartbit4all.api.view.restserver.ViewApiController;
import org.smartbit4all.api.view.restserver.ViewApiDelegate;
import org.smartbit4all.api.view.restserver.impl.ViewApiDelegateImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiCommonSrvRestConfig.class})
public class ViewSrvRestConfig {

  @Bean
  public ViewApiDelegate viewApiDelegate() {
    return new ViewApiDelegateImpl();
  }

  @Bean
  public ViewApiController viewApiController(ViewApiDelegate delegate) {
    return new ViewApiController(delegate);
  }

  @Bean
  @ConditionalOnMissingBean(ViewApi.class)
  public ViewApi viewApi() {
    return new ViewApiImpl();
  }

  @Bean
  @ConditionalOnMissingBean(ViewContextService.class)
  public ViewContextService viewContextService() {
    return new ViewContextServiceImpl();
  }

}
