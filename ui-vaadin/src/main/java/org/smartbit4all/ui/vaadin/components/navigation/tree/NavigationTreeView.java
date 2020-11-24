package org.smartbit4all.ui.vaadin.components.navigation.tree;

import java.util.List;
import java.util.stream.Stream;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.ui.common.navigation.NavigationController;
import org.smartbit4all.ui.common.navigation.NavigationTreeNode;
import org.smartbit4all.ui.common.navigation.NavigationView;
import org.smartbit4all.ui.common.view.UIViewShowCommand;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.router.QueryParameters;

public class NavigationTreeView implements NavigationView {

  /**
   * The tree component that visualize the navigation nodes.
   */
  private TreeGrid<NavigationTreeNode> tree;

  /**
   * The controller for accessing the presentation logic.
   */
  NavigationController controller;

  public NavigationTreeView(NavigationController controller,
      TreeGrid<NavigationTreeNode> treeComponent) {
    super();
    this.tree = treeComponent;
    this.controller = controller;
    controller.setUI(this);
    // Adapt the given tree and add the necessary parameters.

    // tree.addColumn(i -> concetanateIconAndName(i.getIconResource(), i.getName())).setHeader("");

    tree.addHierarchyColumn(i -> i.getCaption());
    tree.addSelectionListener(selection -> {
      selection.getFirstSelectedItem().ifPresent(s -> {
        UIViewShowCommand showCommand = controller.getViewCommand(s);
        if (showCommand != null) {
          QueryParameters params = new QueryParameters(showCommand.getParameters());
          tree.getUI().ifPresent(ui -> ui.navigate(showCommand.getViewName(), params));
        }
        // if (s.getEntry().getViews() != null && !s.getEntry().getViews().isEmpty()) {
        // // TODO Here we need a more sophisticated solution to offer the potential views.
        // org.smartbit4all.api.navigation.bean.NavigationView navigationView =
        // s.getEntry().getViews().get(0);
        // controller.setupViewParameters(s);
        // tree.getUI().ifPresent(ui -> ui.navigate(navigationView.getName()));
        // }
      });
    });

    setDataProviderForTree();

  }

  private void setDataProviderForTree() {
    HierarchicalDataProvider<NavigationTreeNode, Void> dataProvider =
        new AbstractBackEndHierarchicalDataProvider<NavigationTreeNode, Void>() {

          @Override
          public int getChildCount(HierarchicalQuery<NavigationTreeNode, Void> query) {
            return controller.getChildCount(query.getParent());
          }

          @Override
          public boolean hasChildren(NavigationTreeNode item) {
            return controller.hasChildren(item);
          }

          @Override
          protected Stream<NavigationTreeNode> fetchChildrenFromBackEnd(
              HierarchicalQuery<NavigationTreeNode, Void> query) {
            return controller.getChildrens(query.getParent());
          }
        };
    tree.setDataProvider(dataProvider);

  }

  @Override
  public void render(NavigationNode node, List<ApiItemChangeEvent<NavigationReference>> changes) {}

}
