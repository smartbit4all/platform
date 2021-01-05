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
package org.smartbit4all.ui.common.navigation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.ui.common.navigation.NavigationTreeNode.Kind;
import org.smartbit4all.ui.common.view.UIViewShowCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class NavigationControllerImpl implements NavigationController {

  Navigation navigationState;

  /**
   * The navigation view.
   */
  protected NavigationView view;

  @Autowired
  protected NavigationApi api;

  /**
   * When starting the navigation we can setup the root nodes. These are the first nodes in the
   * {@link Navigation} instance.
   */
  private List<NavigationNode> rootNodes = new ArrayList<>();

  /**
   * The view options for the navigation.
   */
  private NavigationViewOption options = new NavigationViewOption();

  public NavigationControllerImpl(NavigationApi api) {
    this.api = api;
  }

  @Override
  public void setUI(NavigationView view) {
    this.view = view;
  }

  /**
   * This function recreates the {@link Navigation} state inside the controller based on the newly
   * set {@link NavigationConfig}. Be careful because it's a restart if we already have a running
   * navigation!
   * 
   * @param config The configuration.
   * @return The newly created navigation instance.
   */
  public Navigation startNavigation(NavigationConfig config) {
    navigationState = new Navigation(config, api);
    return navigationState;
  }

  @Override
  public void addRoot(URI entryMetaUri, URI rootObjectURI) {
    if (navigationState != null) {
      rootNodes.add(navigationState.addRootNode(entryMetaUri, rootObjectURI));
    }
  }

  @Override
  public boolean hasChildren(NavigationTreeNode node) {
    if (node == null) {
      return !rootNodes.isEmpty();
    }
    NavigationNode navigationNode = navigationState.getNode(node.getIdentifier());
    if (navigationNode != null) {
      navigationState.expandAll(navigationNode);
    }
    return navigationState.hasChildren(node.getIdentifier());
  }

  @Override
  public int getChildCount(NavigationTreeNode node) {
    // return node == null ? rootNodes.size() :
    // navigationState.numberOfChildren(node.getIdentifier());
    if (node == null) {
      return rootNodes.size();
    }
    NavigationNode navigationNode = navigationState.getNode(node.getIdentifier());
    if (navigationNode != null) {
      navigationState.expandAll(navigationNode);
    }
    return navigationState.numberOfChildren(node.getIdentifier());
  }

  @Override
  public Stream<NavigationTreeNode> getChildren(NavigationTreeNode parent) {
    if (parent == null) {
      return rootNodes.stream().map(
          n -> new NavigationTreeNode(Kind.ENTRY, n.getId(), n.getEntry().getName(), null,
              n.getEntry().getIcon(), null));
    }
    // Kind kind, String identifier, String caption, String shortDescription,
    // String icon, String[] styles
    NavigationNode node = navigationState.getNode(parent.getIdentifier());
    if (node != null) {
      navigationState.expandAll(node);
    }
    return navigationState.getChildren(parent.getIdentifier()).stream()
        .map(n -> new NavigationTreeNode(Kind.ENTRY, n.getId(), n.getEntry().getName(), null,
            n.getEntry().getIcon(), null));

  }

  @Override
  public UIViewShowCommand getViewCommand(NavigationTreeNode node) {
    if (node.isKind(Kind.ENTRY)) {
      NavigationNode navigationNode = navigationState.getNode(node.getIdentifier());
      if (navigationNode != null && navigationNode.getEntry().getViews() != null
          && !navigationNode.getEntry().getViews().isEmpty()) {
        
        NavigationEntry navigationEntry = navigationNode.getEntry();
        org.smartbit4all.api.navigation.bean.NavigationView defaulView = navigationEntry.getViews().get(0);
        
        UIViewShowCommand viewCommand = new UIViewShowCommand(defaulView.getName());
        viewCommand.addParameter("entry", navigationEntry.getObjectUri());
        viewCommand.addParameter("icon", navigationEntry.getIcon());
        Map<String, Object> viewParams = defaulView.getParameters();
        if(viewParams != null) {
          viewCommand.getParameters().putAll(viewParams);
        }
        return viewCommand;
      }
    }
    return null;
  }

}
