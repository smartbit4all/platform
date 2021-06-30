package org.smartbit4all.ui.common.userselector.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.smartbit4all.api.userselector.bean.UserSelectors;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.userselector.UserSelectorViewModel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class UserSelectorViewModelConfiguration {
  
  private Map<Class<?>, ApiBeanDescriptor> userSelectorsDescriptor;

  public UserSelectorViewModelConfiguration() {
    Set<Class<?>> ruleDomainBeans = new HashSet<>();

    ruleDomainBeans.add(UserSelectors.class);
    ruleDomainBeans.add(UserSelector.class);

    userSelectorsDescriptor = ApiBeanDescriptor.of(ruleDomainBeans);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public UserSelectorViewModel userSelectorViewModel(OrgApi orgApi) {
    return new UserSelectorViewModelImpl(orgApi, userSelectorsDescriptor);
  }
}
