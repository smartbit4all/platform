package org.smartbit4all.ui.common.navigation;

import java.util.stream.Stream;
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

  void setRoot(NavigationNode root);

  int getChildCount(NavigationNode node);

  Stream<NavigationNode> getChildrens(NavigationNode parent);

  boolean hasChildren(NavigationNode node);

}
