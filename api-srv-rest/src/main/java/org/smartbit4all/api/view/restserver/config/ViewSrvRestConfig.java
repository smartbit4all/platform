package org.smartbit4all.api.view.restserver.config;

import org.smartbit4all.api.restserver.PlatformApiCommonSrvRestConfig;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.ViewApiImpl;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.ViewContextServiceImpl;
import org.smartbit4all.api.view.ViewRegistryApi;
import org.smartbit4all.api.view.ViewRegistryApiImpl;
import org.smartbit4all.api.view.restserver.ViewApiController;
import org.smartbit4all.api.view.restserver.ViewApiDelegate;
import org.smartbit4all.api.view.restserver.impl.ViewApiDelegateImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
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
  @ConditionalOnMissingClass("org.smartbit4all.api.view.ViewApi")
  public ViewApi viewApi() {
    return new ViewApiImpl();
  }

  @Bean
  @ConditionalOnMissingClass("org.smartbit4all.api.view.ViewRegistryApi")
  public ViewRegistryApi viewRegistryApi() {
    return new ViewRegistryApiImpl();
  }

  @Bean
  @ConditionalOnMissingClass("org.smartbit4all.api.view.ViewContextService")
  public ViewContextService viewContextService() {
    return new ViewContextServiceImpl();
  }

}
