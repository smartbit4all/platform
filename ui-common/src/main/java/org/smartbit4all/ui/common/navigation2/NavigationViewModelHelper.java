package org.smartbit4all.ui.common.navigation2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.tree.model.TreeModel;
import org.smartbit4all.ui.api.tree.model.TreeNode;

public class NavigationViewModelHelper {

  private NavigationViewModelHelper() {}

  public static Map<Class<?>, ApiBeanDescriptor> NAVIGATION_DESCRIPTORS =
      initNavigationDescriptors();

  private static Map<Class<?>, ApiBeanDescriptor> initNavigationDescriptors() {
    Set<Class<?>> domainBeans = collectDomainBeans();

    return ApiBeanDescriptor.of(domainBeans);
  }

  public static Set<Class<?>> collectDomainBeans() {
    Set<Class<?>> domainBeans = new HashSet<>();

    domainBeans.add(TreeModel.class);
    domainBeans.add(TreeNode.class);
    return domainBeans;
  }

}
