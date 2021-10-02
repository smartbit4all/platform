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
package org.smartbit4all.ui.common.navigation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.NavigationConfig;
import org.smartbit4all.api.navigation.bean.NavigationAssociation;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.common.action.Action;
import org.smartbit4all.ui.common.action.Actions;
import org.smartbit4all.ui.common.action.NavigationAction;
import org.smartbit4all.ui.common.action.NavigationActionListener;
import org.smartbit4all.ui.common.navigation.NavigationTreeNode.Kind;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;
import io.reactivex.rxjava3.disposables.Disposable;

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


  @Autowired
  protected Actions actions;

  private NavigationTreeNode selectedNode;

  private List<NavigationActionListener> actionListeners = new ArrayList<>();

  private static NavigationTreeNode treeNodeOf(NavigationNode node) {
    return new NavigationTreeNode(
        Kind.ENTRY,
        node.getId(),
        node.getEntry().getName(),
        null,
        node.getEntry().getIcon(),
        null,
        node.getEntry().getActions());
  }

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
    return getChildCount(node) > 0;
  }

  @Override
  public int getChildCount(NavigationTreeNode node) {
    // return node == null ? rootNodes.size() :
    // navigationState.numberOfChildren(node.getIdentifier());
    if (node == null) {
      return rootNodes.size();
    }
    if (node.isKind(Kind.ASSOCIATION)) {
      // In case of association we have the references as counter.
      return navigationState.numberOfReferences(node.getIdentifier());
    }
    NavigationNode navigationNode = navigationState.getNode(node.getIdentifier());
    if (navigationNode != null) {
      navigationState.expandAll(navigationNode);
    }
    return navigationState.numberOfChildren(node.getIdentifier(), true);
  }

  @Override
  public Stream<NavigationTreeNode> getChildren(NavigationTreeNode parent) {
    if (parent == null) {
      return rootNodes.stream().map(NavigationControllerImpl::treeNodeOf);
    }

    // Kind kind, String identifier, String caption, String shortDescription,
    // String icon, String[] styles
    if (parent.isKind(Kind.ASSOCIATION)) {
      return navigationState.getReferencedNodes(parent.getIdentifier())
          .stream()
          .map(NavigationControllerImpl::treeNodeOf);
    }

    NavigationNode node = navigationState.getNode(parent.getIdentifier());
    if (node != null) {
      navigationState.expandAll(node);
    }

    List<NavigationAssociation> associations =
        node.getAssociations() == null ? Collections.emptyList() : node.getAssociations();

    // create map with nulls to define the order
    LinkedHashMap<String, List<NavigationTreeNode>> treeNodesByOrderedAssocIds =
        new LinkedHashMap<>();
    associations.forEach(a -> {
      treeNodesByOrderedAssocIds.put(a.getId(), null);
    });

    Map<String, List<NavigationNode>> nodesByAssocIds =
        navigationState.getChildrenNodes(parent.getIdentifier(), true);
    nodesByAssocIds.forEach((assocId, nodes) -> {
      List<NavigationTreeNode> treeNodes = nodes.stream()
          .map(n -> new NavigationTreeNode(Kind.ENTRY, n.getId(), n.getEntry().getName(), null,
              n.getEntry().getIcon(), null, n.getEntry().getActions()))
          .collect(Collectors.toList());
      treeNodesByOrderedAssocIds.put(assocId, treeNodes);
    });


    // TODO Correct name for the association
    associations.stream()
        .filter(a -> !a.getHidden())
        .forEach(navigationAssociation -> {
          String[] styles = null;
          if (navigationAssociation.getReferences() == null
              || navigationAssociation.getReferences().isEmpty()) {
            styles = new String[] {"empty"};
          }

          NavigationTreeNode treeNode = new NavigationTreeNode(
              Kind.ASSOCIATION,
              navigationAssociation.getId(),
              getAssociationNodeCaption(navigationAssociation),
              null,
              navigationAssociation.getIcon(),
              styles,
              null);

          treeNodesByOrderedAssocIds.put(navigationAssociation.getId(),
              Collections.singletonList(treeNode));
        });


    List<NavigationTreeNode> resultTreeNodes = new ArrayList<>();
    treeNodesByOrderedAssocIds.forEach((assoc, nodes) -> {
      if (nodes != null) {
        resultTreeNodes.addAll(nodes);
      }
    });
    return resultTreeNodes.stream();

  }

  private String getAssociationNodeCaption(NavigationAssociation association) {
    String associationCaption = association.getCaption();
    if (Strings.isNullOrEmpty(associationCaption)) {
      return getAssociationNodeCaption(association.getMetaUri());
    } else {
      return association.getCaption();
    }
  }

  private String getAssociationNodeCaption(URI assocMetaUri) {
    String assocUriString = assocMetaUri.toString();
    if (assocUriString.contains("?")) {
      assocUriString = assocUriString.substring(0, assocUriString.indexOf("?"));
    }
    String caption = assocUriString
        .replace(":/", ".")
        .replace("/", ".")
        .replace("#", ".");
    return caption;
  }

  @Override
  public NavigationTarget getViewCommand(NavigationTreeNode node) {
    if (node.isKind(Kind.ENTRY)) {

      NavigationNode navigationNode = navigationState.getNode(node.getIdentifier());
      if (hasNavigationView(navigationNode)) {
        NavigationTarget command = createNavigationViewShowCommand(navigationNode);

        addRootNodeParameter(navigationNode, command);

        return command;
      }

    } else if (node.isKind(Kind.ASSOCIATION)) {

      NavigationAssociation assoc = navigationState.getAssociation(node.getIdentifier());
      NavigationNode naviNode = navigationState.getNode(assoc.getNodeId());

      // NavigationNode naviNode = assoc.getNode();
      if (naviNode != null) {
        if (hasNavigationView(naviNode)) {
          NavigationTarget command = createNavigationViewShowCommand(naviNode);
          command.putParametersItem(Navigation.ASSOC_URI_VIEW_PARAM_KEY, assoc.getMetaUri());

          return command;
        }
      }

    }

    return null;
  }

  private void addRootNodeParameter(NavigationNode navigationNode, NavigationTarget command) {
    NavigationNode rootNode = navigationState.getRootNode(navigationNode);
    if (rootNode != null &&
        rootNode.getEntry() != null &&
        rootNode.getEntry().getObjectUri() != null) {

      URI rootObjectEntryUri = rootNode.getEntry().getObjectUri();
      command.putParametersItem(Navigation.ROOT_OBJECT_URI, rootObjectEntryUri);
    }
  }

  private boolean hasNavigationView(NavigationNode navigationNode) {
    return navigationNode != null && navigationNode.getEntry().getViews() != null
        && !navigationNode.getEntry().getViews().isEmpty();
  }

  private NavigationTarget createNavigationViewShowCommand(NavigationNode navigationNode) {
    NavigationEntry navigationEntry = navigationNode.getEntry();
    org.smartbit4all.api.navigation.bean.NavigationView defaulView =
        navigationEntry.getViews().get(0);

    NavigationTarget viewCommand = new NavigationTarget()
        .viewName(defaulView.getName())
        .putParametersItem("entry", navigationEntry.getObjectUri())
        .putParametersItem("icon", navigationEntry.getIcon());
    Map<String, Object> viewParams = defaulView.getParameters();
    if (viewParams != null) {
      viewCommand.getParameters().putAll(viewParams);
    }

    return viewCommand;
  }

  @Override
  public void nodeSelected(NavigationTreeNode node) {
    view.navigateTo(getViewCommand(node));

    showNodeActions(node);
    selectedNode = node;
  }

  @Override
  public void refreshNode(NavigationTreeNode node) {
    if (node.isKind(Kind.ASSOCIATION)) {
      navigationState.forceRefreshAssociation(node.getIdentifier());
    } else if (node.isKind(Kind.ENTRY)) {
      navigationState.forceRefreshEntry(node.getIdentifier());
    }
  }

  private void showNodeActions(NavigationTreeNode node) {
    List<Action> actionsToHide = getNodeActions(selectedNode);
    List<Action> actionsToShow = getNodeActions(node);
    for (NavigationActionListener navigationActionListener : actionListeners) {
      navigationActionListener.onHideActions(actionsToHide);
      navigationActionListener.onShowActions(actionsToShow);
    }
  }

  private List<Action> getNodeActions(NavigationTreeNode node) {
    List<Action> actionList = new ArrayList<Action>();
    if (node != null && node.getActions() != null) {
      for (URI uri : node.getActions()) {
        Action action = actions.get(uri);
        if (action != null) {
          actionList.add(action);

          if (action instanceof NavigationAction) {
            ((NavigationAction) action).setNavigationController(this);
          }
        }
      }
    }
    return actionList;
  }

  @Override
  public void registerNavigationActionListener(NavigationActionListener listener) {
    actionListeners.add(listener);
  }

  @Override
  public NavigationTreeNode getSelectedNode() {
    return selectedNode;
  }

  @Override
  public NavigationEntry getNavigationEntry(NavigationTreeNode navigationTreeNode) {
    if (navigationTreeNode.isKind(Kind.ENTRY)) {
      NavigationNode navigationNode = navigationState.getNode(navigationTreeNode.getIdentifier());
      if (navigationNode != null) {

        NavigationEntry navigationEntry = navigationNode.getEntry();
        return navigationEntry;
      }
    }
    return null;
  }

  @Override
  public NavigationEntry getSelectedEntry() {
    return getNavigationEntry(selectedNode);
  }

  @Override
  public void navigateTo(NavigationTarget command) {
    view.navigateTo(command);
  }

  @Override
  public Disposable subscribeForNodeRefresh(Consumer<NavigationTreeNode> listener) {
    return navigationState.subscribeForNodeRefresh(node -> listener.accept(treeNodeOf(node)));
  }

  @Override
  public Disposable subscribeForRootNodeAdded(Consumer<NavigationTreeNode> listener) {
    return navigationState.subscribeForRootNodeAdded(node -> listener.accept(treeNodeOf(node)));
  }

  @Override
  public Disposable subscribeForRootNodeRemoved(Consumer<NavigationTreeNode> listener) {
    return navigationState.subscribeForRootNodeRemoved(node -> listener.accept(treeNodeOf(node)));
  }

}
