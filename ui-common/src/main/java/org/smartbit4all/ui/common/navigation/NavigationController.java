package org.smartbit4all.ui.common.navigation;

import java.net.URI;
import java.util.stream.Stream;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import org.smartbit4all.ui.common.controller.UIController;
import org.smartbit4all.ui.common.view.UIViewShowCommand;

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

  void addRoot(NavigationEntryMeta entryMeta, URI rootObjectURI);

  int getChildCount(NavigationTreeNode node);

  Stream<NavigationTreeNode> getChildrens(NavigationTreeNode parent);

  boolean hasChildren(NavigationTreeNode node);

  UIViewShowCommand getViewCommand(NavigationTreeNode node);

}
