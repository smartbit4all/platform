package org.smartbit4all.api.view.restserver.config;

import org.smartbit4all.api.grid.restserver.GridApiController;
import org.smartbit4all.api.grid.restserver.GridApiDelegate;
import org.smartbit4all.api.grid.restserver.impl.GridApiDelegateImpl;
import org.smartbit4all.api.restserver.PlatformApiCommonSrvRestConfig;
import org.smartbit4all.api.uitree.restserver.TreeApiController;
import org.smartbit4all.api.uitree.restserver.TreeApiDelegate;
import org.smartbit4all.api.uitree.restserver.impl.TreeApiDelegateImpl;
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
  @ConditionalOnMissingBean
  public ViewApi viewApi() {
    return new ViewApiImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public ViewContextService viewContextService() {
    return new ViewContextServiceImpl();
  }

  @Bean
  public TreeApiDelegate treeApiDelegate() {
    return new TreeApiDelegateImpl();
  }

  @Bean
  public TreeApiController treeApiController(TreeApiDelegate delegate) {
    return new TreeApiController(delegate);
  }

  @Bean
  public GridApiDelegate gridApiDelegate() {
    return new GridApiDelegateImpl();
  }

  @Bean
  public GridApiController gridApiController(GridApiDelegate delegate) {
    return new GridApiController(delegate);
  }

}
