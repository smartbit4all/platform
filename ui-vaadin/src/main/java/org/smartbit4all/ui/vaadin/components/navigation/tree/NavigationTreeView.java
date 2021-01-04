/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components.navigation.tree;

import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.ApiItemChangeEvent;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.ui.common.navigation.NavigationController;
import org.smartbit4all.ui.common.navigation.NavigationTreeNode;
import org.smartbit4all.ui.common.navigation.NavigationView;
import org.smartbit4all.ui.common.view.UIViewShowCommand;
import org.smartbit4all.ui.vaadin.view.UIViewParameterVaadinTransition;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

public class NavigationTreeView implements NavigationView {

  private static Logger log = LoggerFactory.getLogger(NavigationTreeView.class);

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

    tree.addHierarchyColumn(i -> i.getCaption()).setAutoWidth(true);
    tree.addSelectionListener(selection -> {
      selection.getFirstSelectedItem().ifPresent(s -> {
        UIViewShowCommand showCommand = controller.getViewCommand(s);
        if (showCommand != null) {
          try (UIViewParameterVaadinTransition param =
              new UIViewParameterVaadinTransition(showCommand.getParameters())) {

            tree.getUI().ifPresent(ui -> ui.navigate(showCommand.getViewName(), param.construct()));
          } catch (Exception e) {
            log.error("Unable to pass parameters", e);
          }
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
            return controller.getChildren(query.getParent());
          }
        };
    tree.setDataProvider(dataProvider);

  }

  @Override
  public void render(NavigationNode node, List<ApiItemChangeEvent<NavigationReference>> changes) {}

}
