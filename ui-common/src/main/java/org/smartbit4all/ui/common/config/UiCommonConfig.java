package org.smartbit4all.ui.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * The spring configurations for the platform services of the ui common.
 * 
 * @author Peter Boros
 */
@Configuration
@EnableAsync
public class UiCommonConfig {

  // public static final String VIEW_PARAM = "viewparam";
  //
  // @Bean
  // public CustomScopeConfigurer viewParameterScopeConfigurer() {
  // SimpleThreadScope scope = new SimpleThreadScope();
  // CustomScopeConfigurer result = new CustomScopeConfigurer();
  // Map<String, Object> scopesToAdd = new HashMap<>();
  // scopesToAdd.put(VIEW_PARAM, scope);
  // result.setScopes(scopesToAdd);
  // return result;
  // }
  //
  // @Scope(VIEW_PARAM)
  // @Bean
  // public UIViewParameter viewParameter() {
  // return new UIViewParameter();
  // }

}
