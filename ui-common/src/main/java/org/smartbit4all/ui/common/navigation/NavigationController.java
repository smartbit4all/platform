package org.smartbit4all.ui.common.navigation;

import java.util.stream.Stream;
import org.smartbit4all.api.navigation.bean.NavigationAssociation;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.ui.common.controller.UIController;

/**
 * The UI controller for a navigation view that contains all the business logic. Instantiated for
 * every navigation view since it's a stateful object.
 * 
 * @author Peter Boros
 */
public interface NavigationController extends UIController {

  /**
   * The view instance
   * 
   * @param view
   */
  void setUI(NavigationView view);

  /**
   * Expand the given node following all the associations we have.
   * 
   * @param entry
   */
  void expandAll(NavigationNode node);

  /**
   * Expand the given association if it's not expanded.
   * 
   * @param association
   */
  void expand(NavigationAssociation association);

  void setRoot(NavigationNode root);

  void loadRootNodes();

  int getChildCount(NavigationNode node);

  Stream<NavigationNode> getChildrens(NavigationNode parent);

  boolean hasChildren(NavigationNode node);

}
