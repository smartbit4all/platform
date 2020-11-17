package org.smartbit4all.ui.common.navigation;

import java.util.List;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.ui.common.view.UIView;

public interface NavigationView extends UIView {

  /**
   * Initialize the view by setting the root navigation node.
   * 
   * @param root
   */
  void setRoot(NavigationNode root);

  void render(NavigationNode node, List<ApiItemChangeEvent<NavigationReference>> changes);

}
