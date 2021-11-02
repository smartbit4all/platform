package org.smartbit4all.ui.api.navigation;

import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationPath;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;

/**
 * The editing model for the navigation. It contains a navigation
 * 
 * @author Peter Boros
 */
public interface NavigationViewModel extends ObjectEditing {

  @PublishEvents("MODEL")
  ObservableObject model();

  void refreshSelectedNode();
  
  NavigationTarget loadNavigationTarget();

  /**
   * Select the node if it's already exists and opened.
   * 
   * @param node
   */
  void setSelectedNode(NavigationNode node);

  /**
   * Open the given path and select the last segment.
   * 
   * @param path
   */
  void setSelectedNode(NavigationPath path);

}
