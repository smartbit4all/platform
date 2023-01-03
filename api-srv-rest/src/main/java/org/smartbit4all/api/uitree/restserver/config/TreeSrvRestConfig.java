package org.smartbit4all.api.uitree.restserver.config;

import org.smartbit4all.api.restserver.PlatformApiCommonSrvRestConfig;
import org.smartbit4all.api.uitree.restserver.TreeApiController;
import org.smartbit4all.api.uitree.restserver.TreeApiDelegate;
import org.smartbit4all.api.uitree.restserver.impl.TreeApiDelegateImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiCommonSrvRestConfig.class})
public class TreeSrvRestConfig {

  @Bean
  public TreeApiDelegate treeApiDelegate() {
    return new TreeApiDelegateImpl();
  }

  @Bean
  public TreeApiController treeApiController(TreeApiDelegate delegate) {
    return new TreeApiController(delegate);
  }

}
