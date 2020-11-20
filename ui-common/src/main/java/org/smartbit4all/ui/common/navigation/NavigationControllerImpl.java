package org.smartbit4all.ui.common.navigation;

import java.util.stream.Stream;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.bean.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.springframework.beans.factory.annotation.Autowired;

public class NavigationControllerImpl implements NavigationController {

  Navigation navigationState;

  /**
   * The navigation view.
   */
  protected NavigationView view;

  @Autowired
  protected NavigationApi api;

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

  // /**
  // * The expand will retrieve the available entries starting from the given entry. The expand will
  // * retrieve the related entries from the underlying api.
  // *
  // * @param entry The entry to expand. The entry will be filled with new entries as children.
  // * @return The api will return the newly created entries for further processing.
  // */
  // List<ApiItemChangeEvent<NavigationEntry>> expand(NavigationEntry entry);
  //
  // /**
  // * This function will retrieve again the navigation entries and it's children. The object
  // * structure will be refreshed but also we get back the list of changes in an
  // * {@link ApiItemChangeEvent} list with the changed {@link NavigationEntry}s.
  // *
  // * @param entry The entry to refresh. The refresh
  // * @return
  // */
  // List<ApiItemChangeEvent<NavigationEntry>> refresh(NavigationEntry entry);

}
