/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.common.config;

import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.ui.api.navigation.NavigationViewModel;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.common.navigation2.NavigationViewModelImpl;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
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

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  NavigationViewModel navigationViewModel(ObservablePublisherWrapper publisherWrapper,
      UINavigationApi uiNavigationApi,
      UserSessionApi userSessionApi) {
    return new NavigationViewModelImpl(publisherWrapper, uiNavigationApi, userSessionApi);
  }

}
