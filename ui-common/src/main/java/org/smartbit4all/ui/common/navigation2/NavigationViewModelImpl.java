package org.smartbit4all.ui.common.navigation2;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.bean.NavigationAssociation;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationPath;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.ui.api.navigation.NavigationViewModel;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.tree.model.TreeModel;
import org.smartbit4all.ui.api.tree.model.TreeNode;
import org.smartbit4all.ui.api.tree.model.TreeNodeKind;
import com.google.common.base.Strings;
import io.reactivex.rxjava3.disposables.Disposable;

public class NavigationViewModelImpl extends ObjectEditingImpl implements NavigationViewModel {

  private TreeModel model;

  private final ObservableObjectImpl modelObservable;

  private Navigation navigationState;

  private TreeNode selectedNode;

  private Map<String, TreeNode> treeNodesById;

  private Map<String, Disposable> subscriptionsById;

  private UINavigationApi uiNavigationApi;

  public NavigationViewModelImpl(Navigation navigation,
      ObservablePublisherWrapper publisherWrapper,
      UINavigationApi uiNavigationApi) {
    this.navigationState = navigation;
    this.uiNavigationApi = uiNavigationApi;
    ref = new ApiObjectRef(null, new TreeModel(),
        NavigationViewModelHelper.getNavigationDescriptors());
    modelObservable = new ObservableObjectImpl(publisherWrapper);
    modelObservable.setRef(ref);
    model = ref.getWrapper(TreeModel.class);
    treeNodesById = new HashMap<>();
    subscriptionsById = new HashMap<>();
    Disposable subscribeForNodeRefresh =
        navigationState.subscribeForNodeRefresh(this::refreshNavigationNode);
    Disposable subscribeForRootNodeAdded =
        navigationState.subscribeForRootNodeAdded(this::rootNodeAdded);
    Disposable subscribeForRootNodeRemoved =
        navigationState.subscribeForRootNodeRemoved(this::rootNodeRemoved);
    subscriptionsById.put("subscribeForNodeRefresh", subscribeForNodeRefresh);
    subscriptionsById.put("subscribeForRootNodeAdded", subscribeForRootNodeAdded);
    subscriptionsById.put("subscribeForRootNodeRemoved", subscribeForRootNodeRemoved);
  }

  @Override
  public ObservableObject model() {
    return modelObservable;
  }

  @Override
  public void executeCommand(String commandPath, String command, Object... params) {
    switch (command) {
      case "expand":
        expand(getTreeNodeByPath(commandPath));
        break;
      case "collapse":
        collapse(getTreeNodeByPath(commandPath));
        break;
      case "select":
        select(getTreeNodeByPath(commandPath));
        break;
      default:
        // super.executeCommand(commandPath, command, params);
        break;
    }
    notifyAllListeners();
  }

  private void expand(TreeNode node) {
    loadChildren(node);
    node.getChildrenNodes().forEach(this::loadChildren);
    node.setExpanded(Boolean.TRUE);
  }

  private void collapse(TreeNode node) {
    node.setExpanded(Boolean.FALSE);
  }

  private void select(TreeNode node) {
    if (selectedNode != null) {
      selectedNode.setSelected(false);
    }
    if (selectedNode == node) {
      selectedNode = null;
    } else {
      selectedNode = node;
      selectedNode.setSelected(true);
      NavigationTarget navigationTarget = getViewCommand(selectedNode);
      node.setNavigationTarget(navigationTarget);
      if (navigationTarget != null) {
        uiNavigationApi.navigateTo(navigationTarget);
      }
    }
  }

  @Override
  public void setSelectedNode(NavigationNode node) {
    select(findTreeNodeById(node.getId()));
    notifyAllListeners();
  }

  @Override
  public void setSelectedNode(NavigationPath path) {
    if (path == null || path.getSegments().isEmpty()) {
      return;
    }
    ListIterator<String> iterSegment = path.getSegments().listIterator();
    if (iterSegment.hasNext()) {
      String firstSegment = iterSegment.next();
      List<NavigationNode> matchingRootNodes = navigationState.getRootNodes().stream()
          .filter(n -> URI.create(firstSegment).equals(n.getEntry().getObjectUri()))
          .collect(Collectors.toList());
      ListIterator<NavigationNode> iterRoots = matchingRootNodes.listIterator();
      if (iterRoots.hasNext()) {
        NavigationNode node = iterRoots.next();
        TreeNode treeNodeById;
        do {
          treeNodeById = findTreeNodeById(node.getId());
          if (treeNodeById == null) {
            break;
          }
          expand(treeNodeById);
          // Now find the association.
          if (!iterSegment.hasNext()) {
            break;
          }
          String assoc = iterSegment.next();
          URI assocMetaUri = navigationState.findAssocMetaUri(node, assoc);

          NavigationAssociation association =
              node.getAssociations().stream().filter(a -> a.getMetaUri().equals(assocMetaUri))
                  .findFirst().orElse(null);

          if (association == null) {
            break;
          }

          treeNodeById = findTreeNodeById(association.getId());
          if (treeNodeById != null) {
            expand(treeNodeById);
          }

          // Step forward to have the next path segment with the node uri.
          if (iterSegment.hasNext()) {
            URI nextObjectUri = URI.create(iterSegment.next());
            NavigationReference nextRef = association.getReferences().stream()
                .filter(r -> r.getEndNode().getEntry().getObjectUri().equals(nextObjectUri))
                .findFirst().orElse(null);
            if (nextRef != null) {
              node = nextRef.getEndNode();
            }
          }
        } while (iterSegment.hasNext());
        if (treeNodeById != null) {
          select(treeNodeById);
        }
      }
      notifyAllListeners();
    }
  }

  @Override
  public void refreshSelectedNode() {
    if (selectedNode != null) {
      if (selectedNode.getKind() == TreeNodeKind.ASSOCIATION) {
        refreshAssociationNode(selectedNode.getIdentifier());
      } else if (selectedNode.getKind() == TreeNodeKind.ENTRY) {
        refreshNavigationNode(selectedNode.getIdentifier());
      }
    }
  }

  private void refreshNavigationNode(String nodeId) {
    navigationState.forceRefreshEntry(nodeId);
    NavigationNode navigationNode = navigationState.getNode(nodeId);
    if (navigationNode != null) {
      navigationState.refreshNavigationEntry(navigationNode);
      navigationState.expandAll(navigationNode, true);
      refreshNavigationNode(navigationNode);
    } else {
      throw new RuntimeException("Node not found: " + treeNodesById.get(nodeId));
    }
  }

  private void refreshAssociationNode(String nodeId) {
    navigationState.forceRefreshAssociation(nodeId);
    NavigationAssociation assoc = navigationState.getAssociation(nodeId);
    if (assoc != null) {
      navigationState.expandAll(assoc, true);
      refreshNavigationAssociation(assoc);
    } else {
      throw new RuntimeException("Assoc not found: " + treeNodesById.get(nodeId));
    }
  }

  private void loadChildren(TreeNode parent) {
    if (!parent.getChildrenNodesLoaded()) {
      Map<String, TreeNode> oldChildren = new HashMap<>();
      for (TreeNode oldChild : parent.getChildrenNodes()) {
        oldChildren.put(oldChild.getIdentifier(), oldChild);
      }
      // parent.getChildrenNodes().clear();
      if (parent.getKind() == TreeNodeKind.ASSOCIATION) {
        navigationState.getReferencedNodes(parent.getIdentifier())
            .stream()
            .map(n -> createOrUpdateTreeNodeEntry(n, parent.getLevel() + 1))
            .forEachOrdered(n -> {
              if (oldChildren.containsKey(n.getIdentifier())) {
                oldChildren.remove(n.getIdentifier());
              } else {
                parent.getChildrenNodes().add(n);
              }
            });
      } else {
        // TreeNodeKind.ENTRY. Other, like reference?

        NavigationNode node = navigationState.getNode(parent.getIdentifier());
        if (node != null) {
          navigationState.expandAll(node);
        }

        List<NavigationAssociation> associations =
            node.getAssociations() == null ? Collections.emptyList() : node.getAssociations();

        // create map with nulls to define the order
        LinkedHashMap<String, List<TreeNode>> treeNodesByOrderedAssocIds =
            new LinkedHashMap<>();
        associations.forEach(a -> {
          treeNodesByOrderedAssocIds.put(a.getId(), null);
        });

        Map<String, List<NavigationNode>> nodesByAssocIds =
            navigationState.getChildrenNodes(parent.getIdentifier(), true);
        nodesByAssocIds.forEach((assocId, nodes) -> {
          List<TreeNode> treeNodes = nodes.stream()
              .map(navNode -> createOrUpdateTreeNodeEntry(navNode, parent.getLevel() + 1))
              .collect(Collectors.toList());
          treeNodesByOrderedAssocIds.put(assocId, treeNodes);
        });


        // TODO Correct name for the association
        associations.stream()
            .filter(a -> !a.getHidden())
            .forEach(navigationAssociation -> {
              treeNodesByOrderedAssocIds.put(navigationAssociation.getId(),
                  Collections.singletonList(
                      createOrUpdateTreeNodeAssoc(navigationAssociation, parent.getLevel() + 1)));
            });

        treeNodesByOrderedAssocIds.forEach((assoc, nodes) -> {
          if (nodes != null) {
            for (TreeNode n : nodes) {
              if (oldChildren.containsKey(n.getIdentifier())) {
                oldChildren.remove(n.getIdentifier());
              } else {
                parent.getChildrenNodes().add(n);
              }
            }
            // parent.getChildrenNodes().addAll(nodes);
          }
        });
      }

      for (TreeNode n : oldChildren.values()) {
        if (selectedNode == n) {
          select(parent);
        }
        parent.getChildrenNodes().remove(n);
      }
      parent.setChildrenNodesLoaded(Boolean.TRUE);
    }
    parent.setHasChildren(parent.getChildrenNodes().size() > 0);
  }

  private TreeNode createOrUpdateTreeNodeEntry(NavigationNode node, int level) {
    String nodeId = node.getId();
    TreeNode treeNode = treeNodesById.get(nodeId);
    if (treeNode == null) {
      treeNode = new TreeNode()
          .kind(TreeNodeKind.ENTRY)
          .identifier(nodeId)
          .caption(node.getEntry().getName())
          .icon(node.getEntry().getIcon())
          .actions(node.getEntry().getActions())
          .level(level);
      treeNodesById.put(nodeId, treeNode);
    } else {
      updateTreeNodeEntry(treeNode, node);
      treeNode.setLevel(level);
    }
    if (!subscriptionsById.containsKey(nodeId)) {
      Disposable disposable =
          navigationState.subscribeNodeForChanges(node, uri -> refreshNavigationNode(nodeId));
      if (disposable != null) {
        subscriptionsById.put(nodeId, disposable);
      }
    }
    return treeNode;
  }

  private TreeNode createOrUpdateTreeNodeAssoc(NavigationAssociation navigationAssociation,
      int level) {
    String assocId = navigationAssociation.getId();
    TreeNode treeNode = treeNodesById.get(assocId);
    if (treeNode == null) {
      treeNode = new TreeNode()
          .kind(TreeNodeKind.ASSOCIATION)
          .identifier(assocId)
          .caption(getAssociationNodeCaption(navigationAssociation))
          .icon(navigationAssociation.getIcon())
          .styles(getNavigationAssociationStyles(navigationAssociation))
          .level(level);
      treeNodesById.put(assocId, treeNode);
    } else {
      updateTreeNodeAssoc(treeNode, navigationAssociation);
      treeNode.setLevel(level);
    }
    if (!subscriptionsById.containsKey(assocId)) {
      NavigationNode parentNode = navigationState.getNode(navigationAssociation.getNodeId());
      Disposable disposable = navigationState.subscribeNodeForChanges(parentNode,
          uri -> refreshAssociationNode(assocId));
      if (disposable != null) {
        subscriptionsById.put(assocId, disposable);
      }
    }
    return treeNode;
  }

  private void updateTreeNodeEntry(TreeNode treeNode, NavigationNode node) {
    treeNode
        .kind(TreeNodeKind.ENTRY)
        .identifier(node.getId())
        .caption(node.getEntry().getName())
        .icon(node.getEntry().getIcon())
        .actions(node.getEntry().getActions());
  }

  private void updateTreeNodeAssoc(TreeNode treeNode, NavigationAssociation navigationAssociation) {
    treeNode
        .kind(TreeNodeKind.ASSOCIATION)
        .identifier(navigationAssociation.getId())
        .caption(getAssociationNodeCaption(navigationAssociation))
        .icon(navigationAssociation.getIcon())
        .styles(getNavigationAssociationStyles(navigationAssociation));
  }

  private TreeNode getTreeNodeByPath(String path) {
    return ref.getValueRefByPath(path).getWrapper(TreeNode.class);
  }

  public void notifyAllListeners() {
    modelObservable.notifyListeners();
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


  private NavigationTarget getViewCommand(TreeNode node) {
    if (node.getKind() == TreeNodeKind.ENTRY) {

      NavigationNode navigationNode = navigationState.getNode(node.getIdentifier());
      if (hasNavigationView(navigationNode)) {
        NavigationTarget command = createNavigationTarget(navigationNode);
        addRootNodeParameter(navigationNode, command);
        return command;
      }

    } else if (node.getKind() == TreeNodeKind.ASSOCIATION) {

      NavigationAssociation assoc = navigationState.getAssociation(node.getIdentifier());
      NavigationNode naviNode = navigationState.getNode(assoc.getNodeId());

      // NavigationNode naviNode = assoc.getNode();
      if (naviNode != null) {
        if (hasNavigationView(naviNode)) {
          NavigationTarget command = createNavigationTarget(naviNode);
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

  private NavigationTarget createNavigationTarget(NavigationNode navigationNode) {
    NavigationEntry navigationEntry = navigationNode.getEntry();
    NavigationView defaulView = navigationEntry.getViews().get(0);

    NavigationTarget target = new NavigationTarget()
        .viewName(defaulView.getName())
        .putParametersItem("entry", navigationEntry.getObjectUri())
        .putParametersItem("icon", navigationEntry.getIcon());
    Map<String, Object> viewParams = defaulView.getParameters();
    if (viewParams != null) {
      target.getParameters().putAll(viewParams);
    }

    return target;
  }

  private void refreshNavigationAssociation(NavigationAssociation navigationAssociation) {
    TreeNode nodeToRefresh = findTreeNodeById(navigationAssociation.getId());
    if (nodeToRefresh != null) {
      nodeToRefresh.setChildrenNodesLoaded(false);
      loadChildren(nodeToRefresh);
      nodeToRefresh.getChildrenNodes().forEach(this::loadChildren);
      updateTreeNodeAssoc(nodeToRefresh, navigationAssociation);
      notifyAllListeners();
    }
  }

  private List<String> getNavigationAssociationStyles(NavigationAssociation navigationAssociation) {
    List<String> styles = new ArrayList<>();
    if (navigationAssociation.getReferences() == null
        || navigationAssociation.getReferences().isEmpty()) {
      styles.add("empty");
    }
    return styles;
  }

  private void refreshNavigationNode(NavigationNode navigationNode) {

    TreeNode nodeToRefresh = findTreeNodeById(navigationNode.getId());
    if (nodeToRefresh != null) {
      nodeToRefresh.setChildrenNodesLoaded(false);
      loadChildren(nodeToRefresh);
      nodeToRefresh.getChildrenNodes().forEach(this::loadChildren);
      updateTreeNodeEntry(nodeToRefresh, navigationNode);
      notifyAllListeners();
    }
  }

  private void rootNodeAdded(NavigationNode rootNode) {
    TreeNode treeNode = createOrUpdateTreeNodeEntry(rootNode, 0);
    loadChildren(treeNode);
    model.getRootNodes().add(treeNode);
    notifyAllListeners();
  }

  private void rootNodeRemoved(NavigationNode rootNode) {
    model.getRootNodes().stream()
        .filter(node -> rootNode.getId().equals(node.getIdentifier()))
        .findFirst()
        .ifPresent(root -> {
          model.getRootNodes().remove(root);
          notifyAllListeners();
        });
  }

  private TreeNode findTreeNodeById(String id) {
    // return treeNodesById.get(id);
    TreeNode result = null;
    for (TreeNode root : model.getRootNodes()) {
      result = findTreeNodeRecursive(root, id);
      if (result != null) {
        return result;
      }
    }
    return result;
  }

  private TreeNode findTreeNodeRecursive(TreeNode node, String id) {
    if (node.getIdentifier().equals(id)) {
      return node;
    }
    for (TreeNode child : node.getChildrenNodes()) {
      TreeNode result = findTreeNodeRecursive(child, id);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  @Override
  public NavigationTarget loadNavigationTarget() {
    if (selectedNode != null) {
      return selectedNode.getNavigationTarget();
    }

    return null;
  }

  @Override
  public NavigationNode selectedNavigationNode() {
    return navigationState.getNode(selectedNode.getIdentifier());
  }

  @Override
  public void onCloseWindow() {
    subscriptionsById.values().forEach(d -> d.dispose());
  }
}
