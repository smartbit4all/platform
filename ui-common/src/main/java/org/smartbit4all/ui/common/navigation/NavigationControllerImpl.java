package org.smartbit4all.ui.common.navigation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.bean.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
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
  public void addRoot(NavigationEntryMeta entryMeta, URI rootObjectURI) {
    if (navigationState != null) {
      rootNodes.add(navigationState.addRootNode(entryMeta, rootObjectURI));
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
  public Stream<NavigationTreeNode> getChildrens(NavigationTreeNode parent) {
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
    return navigationState.getCildrens(parent.getIdentifier()).stream()
        .map(n -> new NavigationTreeNode(Kind.ENTRY, n.getId(), n.getEntry().getName(), null,
            n.getEntry().getIcon(), null));

  }

  @Override
  public UIViewShowCommand getViewCommand(NavigationTreeNode node) {
    if (node.isKind(Kind.ENTRY)) {
      NavigationNode navigationNode = navigationState.getNode(node.getIdentifier());
      if (navigationNode != null && navigationNode.getEntry().getViews() != null
          && !navigationNode.getEntry().getViews().isEmpty()) {
        UIViewShowCommand viewCommand =
            new UIViewShowCommand(navigationNode.getEntry().getViews().get(0).getName());
        viewCommand.addParameter("entry",
            navigationNode.getEntry().getObjectUri());
        return viewCommand;
      }
    }
    return null;
  }

}
