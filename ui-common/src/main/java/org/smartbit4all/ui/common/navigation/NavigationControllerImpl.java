package org.smartbit4all.ui.common.navigation;

import java.util.stream.Stream;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.bean.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class NavigationControllerImpl implements NavigationController {

  Navigation navigationState;

  /**
   * The navigation view.
   */
  protected NavigationView view;

  @Autowired
  protected NavigationApi api;

  @Autowired
  protected ApplicationContext ctx;

  private NavigationNode root;

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
  public void setRoot(NavigationNode root) {
    this.root = root;
    if (navigationState != null) {
      navigationState.setRoot(root);
    }
  }

  @Override
  public boolean hasChildren(NavigationNode node) {
    NavigationNode nodeToProcess = node == null ? root : node;
    navigationState.expandAll(nodeToProcess);
    return navigationState.hasChildren(nodeToProcess.getId());
  }

  @Override
  public int getChildCount(NavigationNode node) {
    NavigationNode nodeToProcess = node == null ? root : node;
    navigationState.expandAll(nodeToProcess);
    return navigationState.numberOfChildren(nodeToProcess.getId());
  }

  @Override
  public Stream<NavigationNode> getChildrens(NavigationNode parent) {
    NavigationNode nodeToProcess = parent == null ? root : parent;
    navigationState.expandAll(nodeToProcess);
    return navigationState.getCildrens(nodeToProcess.getId()).stream();

  }

  @Override
  public void setupViewParameters(NavigationNode node) {
    NavigationViewParameter param = ctx.getBean(NavigationViewParameter.class);
    param.setEntryUri(node.getEntry().getUri());
  }

}
