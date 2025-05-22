package org.smartbit4all.ui.api.navigation.restserver.config;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.restserver.ViewModelApiController;
import org.smartbit4all.ui.api.navigation.restserver.ViewModelApiDelegate;
import org.smartbit4all.ui.api.navigation.restserver.impl.ViewModelApiDelegateImpl;
import org.smartbit4all.ui.common.api.UINavigationApiHeadless;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

/**
 * UI Api rest server config.
 * 
 */
@Configuration
@Import({PlatformApiConfig.class})
public class UIApiSrvRestConfig {

  @Bean
  public ViewModelApiDelegate invocationApiDelegate(UINavigationApi uiNavigationApi) {
    return new ViewModelApiDelegateImpl(uiNavigationApi);
  }

  @Bean
  public ViewModelApiController viewModelApiController(ViewModelApiDelegate viewModelApiDelegate) {
    return new ViewModelApiController(viewModelApiDelegate);
  }

  @Bean
  @Primary
  UINavigationApi uiNavigationApi(@Autowired(required = false) SessionApi sessionApi) {
    return new UINavigationApiHeadless(sessionApi);
  }

  @Bean
  public ProviderApiInvocationHandler<UINavigationApi> uiNavigationApiProvider(
      UINavigationApi api) {
    return Invocations.asProvider(UINavigationApi.class, api);
  }
}
