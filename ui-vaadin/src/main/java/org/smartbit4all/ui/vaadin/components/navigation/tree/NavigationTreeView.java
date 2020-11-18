package org.smartbit4all.ui.vaadin.components.navigation.tree;

import java.util.List;
import java.util.stream.Stream;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.ui.common.navigation.NavigationController;
import org.smartbit4all.ui.common.navigation.NavigationView;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

public class NavigationTreeView implements NavigationView {

  /**
   * The tree component that visualize the navigation nodes.
   */
  private TreeGrid<NavigationNode> tree;

  /**
   * The controller for accessing the presentation logic.
   */
  NavigationController controller;

  NavigationNode root;

  public NavigationTreeView(NavigationController controller,
      TreeGrid<NavigationNode> treeComponent) {
    super();
    this.tree = treeComponent;
    this.controller = controller;
    controller.setUI(this);
    // Adapt the given tree and add the necessary parameters.

    // tree.addSelectionListener(selection -> controller.treeNodeSelected(selection));
    // tree.addColumn(i -> concetanateIconAndName(i.getIconResource(), i.getName())).setHeader("");

    tree.addExpandListener(e -> e.getItems().stream().forEach(n -> controller.expandAll(n)));
    tree.addHierarchyColumn(i -> i.getEntry().getName());
    setDataProviderForTree();
  }

  private void setDataProviderForTree() {
    HierarchicalDataProvider<NavigationNode, Void> dataProvider =
        new AbstractBackEndHierarchicalDataProvider<NavigationNode, Void>() {

          @Override
          public int getChildCount(HierarchicalQuery<NavigationNode, Void> query) {
            return controller.getChildCount(query.getParent());
          }

          @Override
          public boolean hasChildren(NavigationNode item) {
            return controller.hasChildren(item);
          }

          @Override
          protected Stream<NavigationNode> fetchChildrenFromBackEnd(
              HierarchicalQuery<NavigationNode, Void> query) {
            return controller.getChildrens(query.getParent());
          }
        };
    tree.setDataProvider(dataProvider);

  }

  // private void loadRootNodes() {
  // controller.loadRootNodes();
  // }

  @Override
  public void setRoot(NavigationNode root) {
    controller.setRoot(root);

  }

  @Override
  public void render(NavigationNode node, List<ApiItemChangeEvent<NavigationReference>> changes) {
    // TODO Auto-generated method stub

  }

}
