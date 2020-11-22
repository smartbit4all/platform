package org.smartbit4all.ui.common.config;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.ui.common.navigation.NavigationViewParameter;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * The spring configurations for the platform services of the ui common.
 * 
 * @author Peter Boros
 */
@Configuration
@EnableAsync
public class UiCommonConfig {

  public static final String VIEW_PARAM = "viewparam";

  @Bean
  public CustomScopeConfigurer viewParameterScopeConfigurer() {
    SimpleThreadScope scope = new SimpleThreadScope();
    CustomScopeConfigurer result = new CustomScopeConfigurer();
    Map<String, Object> scopesToAdd = new HashMap<>();
    scopesToAdd.put(VIEW_PARAM, scope);
    result.setScopes(scopesToAdd);
    return result;
  }

  @Bean
  @Scope(VIEW_PARAM)
  public NavigationViewParameter viewParameterThreadLocal() {
    return new NavigationViewParameter();
  }

}
