package org.smartbit4all.ui.common.navigation2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.tree.model.TreeModel;
import org.smartbit4all.ui.api.tree.model.TreeNode;

public class NavigationViewModelHelper {

  private static Map<Class<?>, ApiBeanDescriptor> navigationDescriptors;

  static {
    Set<Class<?>> beans = new HashSet<>();
    beans.add(TreeModel.class);
    beans.add(TreeNode.class);
    navigationDescriptors = ApiBeanDescriptor.of(beans);
  }

  private NavigationViewModelHelper() {}

  public static Map<Class<?>, ApiBeanDescriptor> getNavigationDescriptors() {
    return navigationDescriptors;
  }

}
