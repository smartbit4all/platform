package org.smartbit4all.ui.common.userselector.impl;

import java.util.Map;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class UserSelectorViewModelConfiguration {

  private Map<Class<?>, ApiBeanDescriptor> userSelectorDescriptor;
  private Map<Class<?>, ApiBeanDescriptor> commandsDescriptor;

  public UserSelectorViewModelConfiguration() {
    userSelectorDescriptor = UserSelectorViewModelUtil.createUserSelectorBean();
    commandsDescriptor = UserSelectorViewModelUtil.createCommandsBean();
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public UserSelectorViewModel userSelectorViewModel(OrgApi orgApi) {
    return new UserSelectorViewModelImpl(orgApi, userSelectorDescriptor, commandsDescriptor);
  }
}
