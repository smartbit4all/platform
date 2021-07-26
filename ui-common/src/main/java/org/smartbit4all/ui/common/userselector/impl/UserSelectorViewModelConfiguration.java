package org.smartbit4all.ui.common.userselector.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.userselector.bean.UserMultiSelector;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.smartbit4all.api.userselector.bean.UserSingleSelector;
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
    Set<Class<?>> ruleDomainBeans = new HashSet<>();

    ruleDomainBeans.add(UserSingleSelector.class);
    ruleDomainBeans.add(UserMultiSelector.class);
    ruleDomainBeans.add(UserSelector.class);
    ruleDomainBeans.add(UserSelectorCommands.class);

    userSelectorDescriptor = ApiBeanDescriptor.of(ruleDomainBeans);
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
