package org.smartbit4all.ui.common.userselector.impl;

import java.util.Map;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.userselector.UserMultiSelectorViewModel;
import org.smartbit4all.ui.api.userselector.UserSingleSelectorViewModel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class UserSelectorViewModelConfiguration {

  private Map<Class<?>, ApiBeanDescriptor> userSelectorDescriptor;

  public UserSelectorViewModelConfiguration() {
    userSelectorDescriptor = UserSelectorViewModelUtil.createUserSelectorBean();
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public UserSingleSelectorViewModel userSingleSelectorViewModel(OrgApi orgApi) {
    return new UserSingleSelectorViewModelImpl(orgApi, userSelectorDescriptor);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public UserMultiSelectorViewModel userMultiSelectorViewModel(OrgApi orgApi) {
    return new UserMultiSelectorViewModelImpl(orgApi, userSelectorDescriptor);
  }
}
