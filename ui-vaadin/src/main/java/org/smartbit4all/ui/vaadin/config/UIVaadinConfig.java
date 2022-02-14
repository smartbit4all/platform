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
package org.smartbit4all.ui.vaadin.config;

import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.common.components.user.UserComponentBaseController;
import org.smartbit4all.ui.common.components.user.UserComponentBaseControllerImpl;
import org.smartbit4all.ui.common.components.user.UserComponentConfiguration;
import org.smartbit4all.ui.common.config.UiCommonConfig;
import org.smartbit4all.ui.vaadin.api.UINavigationVaadinRouting;
import org.smartbit4all.ui.vaadin.components.user.UserComponentBase;
import org.smartbit4all.ui.vaadin.object.VaadinPublisherWrapper;
import org.smartbit4all.ui.vaadin.service.DefaultUserComponentFactory;
import org.smartbit4all.ui.vaadin.service.UserComponentFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.UIScope;

@Configuration
@Import(UiCommonConfig.class)
public class UIVaadinConfig {

  @ConditionalOnBean(UserComponentConfiguration.class)
  @Bean
  public UserComponentFactory userComponentFactoryConfigurable(
      ObjectFactory<UserComponentBaseController> controllerFactory) {
    return () -> new UserComponentBase(controllerFactory.getObject());
  }

  @ConditionalOnMissingBean(UserComponentBaseController.class)
  @ConditionalOnBean(UserComponentConfiguration.class)
  @Bean
  @Scope("prototype")
  public UserComponentBaseController userComponentController(UserComponentConfiguration config) {
    return new UserComponentBaseControllerImpl(config);
  }

  @ConditionalOnMissingBean
  @Bean
  public UserComponentFactory userComponentFactoryDefault() {
    return new DefaultUserComponentFactory();
  }

  @Bean
  @UIScope
  public UINavigationApi uiNavigationApi(UI ui,
      @Autowired(required = false) UserSessionApi userSessionApi) {
    return new UINavigationVaadinRouting(ui, userSessionApi);
  }

  @Bean
  @UIScope
  public ObservablePublisherWrapper publisherWrapper(UI ui) {
    return new VaadinPublisherWrapper(ui);
  }

}
