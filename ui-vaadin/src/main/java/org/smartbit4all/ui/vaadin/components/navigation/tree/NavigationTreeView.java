package org.smartbit4all.ui.vaadin.components.navigation.tree;

import java.util.List;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.ui.common.navigation.NavigationController;
import org.smartbit4all.ui.common.navigation.NavigationView;
import com.vaadin.flow.component.treegrid.TreeGrid;

public class NavigationTreeView implements NavigationView {

  /**
   * The tree component that visualize the navigation nodes.
   */
  private TreeGrid<NavigationNode> tree;

  /**
   * The controller for accessing the presentation logic.
   */
  NavigationController controller;

  public NavigationTreeView(NavigationController controller,
      TreeGrid<NavigationNode> treeComponent) {
    super();
    this.tree = treeComponent;
    // Adapt the given tree and add the necessary parameters.

    // tree.addSelectionListener(selection -> controller.treeNodeSelected(selection));
    // tree.addColumn(i -> concetanateIconAndName(i.getIconResource(), i.getName())).setHeader("");
    tree.addExpandListener(e -> e.getItems().stream().forEach(n -> controller.expandAll(n)));
    // tree.addHierarchyColumn(i -> i.getName());
  }

  @Override
  public void setRoot(NavigationNode root) {
    // TODO Auto-generated method stub

  }

  @Override
  public void render(NavigationNode node, List<ApiItemChangeEvent<NavigationReference>> changes) {
    // TODO Auto-generated method stub

  }

}
