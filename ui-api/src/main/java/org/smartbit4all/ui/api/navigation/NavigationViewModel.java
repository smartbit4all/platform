package org.smartbit4all.ui.api.navigation;

import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationPath;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.viewmodel.ViewModel;

/**
 * The editing model for the navigation. It contains a navigation
 * 
 * @author Peter Boros
 */
public interface NavigationViewModel extends ViewModel {

  public static final String EXPAND = "tree.expand";
  public static final String COLLAPSE = "tree.collapse";
  public static final String TOGGLE = "tree.toggle";
  public static final String SELECT = "tree.select";

  public static final String OBJECT_URI_TO_SELECT = "NavigationViewModel.OBJECT_URI_TO_SELECT";

  public static final String NAVIGATION_NAME = "NavigationViewModel.NAVIGATION_NAME";

  void refreshSelectedNode();

  NavigationTarget loadNavigationTarget();

  /**
   * Select the node if it's already exists and opened. If node is null or a treeNode couldn't be
   * found by node, selection will change to empty (unselect).
   * 
   * @param node
   */
  void setSelectedNode(NavigationNode node);

  /**
   * Open the given path and select the last segment. If a treeNode is not found on the path,
   * selection won't change!
   * 
   * @param path
   */
  void setSelectedNodeByPath(NavigationPath path);

  NavigationNode selectedNavigationNode();

}
