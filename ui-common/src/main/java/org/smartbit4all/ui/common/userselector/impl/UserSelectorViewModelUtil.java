package org.smartbit4all.ui.common.userselector.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.userselector.bean.UserMultiSelector;
import org.smartbit4all.api.userselector.bean.UserSelector;
import org.smartbit4all.api.userselector.bean.UserSingleSelector;
import org.smartbit4all.core.object.ApiBeanDescriptor;

public class UserSelectorViewModelUtil {

  public static Map<Class<?>, ApiBeanDescriptor> createUserSelectorBean() {
    Set<Class<?>> ruleDomainBeans = new HashSet<>();

    ruleDomainBeans.add(UserSingleSelector.class);
    ruleDomainBeans.add(UserMultiSelector.class);
    ruleDomainBeans.add(UserSelector.class);
    ruleDomainBeans.add(UserSelectorCommands.class);

    return ApiBeanDescriptor.of(ruleDomainBeans);
  }
}
