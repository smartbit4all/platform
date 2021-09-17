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
    Set<Class<?>> userSelectorBeans = new HashSet<>();

    userSelectorBeans.add(UserSingleSelector.class);
    userSelectorBeans.add(UserMultiSelector.class);
    userSelectorBeans.add(UserSelector.class);
    
    return ApiBeanDescriptor.of(userSelectorBeans);
  }
  
  public static Map<Class<?>, ApiBeanDescriptor> createCommandsBean() {
    Set<Class<?>> commandBeans = new HashSet<>();
    
    commandBeans.add(UserSelectorCommands.class);
    commandBeans.add(String.class);
    
    return ApiBeanDescriptor.of(commandBeans);
  }
}
