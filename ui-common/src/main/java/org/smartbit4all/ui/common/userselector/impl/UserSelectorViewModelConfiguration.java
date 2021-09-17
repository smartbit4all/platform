package org.smartbit4all.ui.common.userselector.impl;

import java.util.Map;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.userselector.UserMultiSelectorViewModel;
import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;
import org.smartbit4all.ui.api.userselector.UserSingleSelectorViewModel;
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
  public UserSingleSelectorViewModel userSingleSelectorViewModel(OrgApi orgApi) {
    return new UserSingleSelectorViewModelImpl(orgApi, userSelectorDescriptor);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public UserMultiSelectorViewModel userMultiSelectorViewModel(OrgApi orgApi) {
    return new UserMultiSelectorViewModelImpl(orgApi, userSelectorDescriptor, commandsDescriptor);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public UserSelectorViewModel userSelectorViewModel(OrgApi orgApi) {
    return new UserSelectorViewModelImpl(orgApi, userSelectorDescriptor, commandsDescriptor);
  }
}
