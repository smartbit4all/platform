package org.smartbit4all.ui.vaadin.config;

import org.smartbit4all.ui.common.components.user.UserComponentBaseController;
import org.smartbit4all.ui.common.components.user.UserComponentBaseControllerImpl;
import org.smartbit4all.ui.common.components.user.UserComponentConfiguration;
import org.smartbit4all.ui.common.config.UiCommonConfig;
import org.smartbit4all.ui.vaadin.components.user.UserComponentBase;
import org.smartbit4all.ui.vaadin.service.DefaultUserComponentFactory;
import org.smartbit4all.ui.vaadin.service.UserComponentFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration
@Import(UiCommonConfig.class)
public class UIVaadinConfig {

  
  @ConditionalOnBean(UserComponentConfiguration.class)
  @Bean
  public UserComponentFactory userComponentFactoryConfigurable(ObjectFactory<UserComponentBaseController> controllerFactory) {
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
}
