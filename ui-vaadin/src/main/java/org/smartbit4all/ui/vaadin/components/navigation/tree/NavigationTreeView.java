/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components.navigation.tree;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.common.navigation.NavigationController;
import org.smartbit4all.ui.common.navigation.NavigationTreeNode;
import org.smartbit4all.ui.common.navigation.NavigationView;
import org.smartbit4all.ui.vaadin.localization.TranslationUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import io.reactivex.rxjava3.disposables.Disposable;

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

  private Disposable subscribeForNodeRefresh;

  private Disposable subscribeForRootNodeAdded;

  private Disposable subscribeForRootNodeRemoved;

  private UI ui;

  private UINavigationApi uiNavigationApi;

  public NavigationTreeView(NavigationController controller,
      TreeGrid<NavigationTreeNode> treeComponent, UINavigationApi uiNavigationApi) {
    super();
    this.tree = treeComponent;
    this.controller = controller;
    this.uiNavigationApi = uiNavigationApi;
    controller.setUI(this);
    // Adapt the given tree and add the necessary parameters.


    tree.addComponentHierarchyColumn(this::getRowComponent).setAutoWidth(true);

    tree.addSelectionListener(selection -> {
      selection.getFirstSelectedItem().ifPresent(s -> {
        controller.nodeSelected(s);
      });
    });

    tree.addItemClickListener(selection -> {
      controller.nodeSelected(selection.getItem());
    });

    setDataProviderForTree();

    subscribeForNodeRefresh = controller.subscribeForNodeRefresh(this::refreshNode);
    subscribeForRootNodeAdded = controller.subscribeForRootNodeAdded(this::refreshAll);
    subscribeForRootNodeRemoved = controller.subscribeForRootNodeRemoved(this::refreshAll);

    // tree.addDetachListener(d -> {
    // if (subscribeForNodeRefresh != null) {
    // subscribeForNodeRefresh.dispose();
    // }
    // if (subscribeForRootNodeAdded != null) {
    // subscribeForRootNodeAdded.dispose();
    // }
    // if (subscribeForRootNodeRemoved != null) {
    // subscribeForRootNodeRemoved.dispose();
    // }
    // });

  }

  private void refreshAll(NavigationTreeNode node) {
    if (ui != null) {
      ui.access(() -> tree.getDataProvider().refreshAll());
    }
  }

  private void refreshNode(NavigationTreeNode node) {
    ui.access(() -> {
      tree.getDataProvider().refreshItem(node, true);
      // tree.collapse(node);
      // tree.expand(node);
    });
    // ui.access(() -> tree.getDataProvider().refreshAll());
  }

  /**
   * Not a final solution. Call this to refresh the selected nodes.
   */
  public void refreshSelectedNodes() {
    for (NavigationTreeNode node : tree.getSelectedItems()) {
      controller.refreshNode(node);
    }
  }

  @Override
  public void navigateTo(NavigationTarget command) {
    if (command != null) {
      uiNavigationApi.navigateTo(command);
    }
  }

  protected Component getRowComponent(NavigationTreeNode node) {
    Objects.requireNonNull(node);
    String iconKey = node.getIcon();
    String title = getNodeTitle(node);
    Label label = new Label(title);
    adjustStylesToNode(node, title, label);
    if (iconKey != null) {
      Icon icon = new Icon(iconKey);
      return new HorizontalLayout(icon, label);
    } else {
      return label;
    }
  }

  protected void adjustStylesToNode(NavigationTreeNode node, String title, Label label) {
    String[] styles = node.getStyles();
    if (styles != null && Arrays.asList(styles).contains("empty")) {
      label.getStyle().set("font-style", "italic");
      label.setText(title + " (0)");
    }
  }

  protected String getNodeTitle(NavigationTreeNode node) {
    String caption = node.getCaption();
    if (caption == null) {
      return "n/a";
    }
    String title = TranslationUtil.INSTANCE().getPossibleTranslation(caption);
    return title;
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

          // @Override
          // public Object getId(NavigationTreeNode item) {
          // return item.getIdentifier();
          // }
        };
    tree.setDataProvider(dataProvider);

  }

  public final UI getUi() {
    return ui;
  }

  public final void setUi(UI ui) {
    this.ui = ui;
  }

}
