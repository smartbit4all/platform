package org.smartbit4all.ui.common.navigation2;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.bean.NavigationAssociation;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.api.navigation.NavigationViewModel;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.tree.model.TreeModel;
import org.smartbit4all.ui.api.tree.model.TreeNode;
import org.smartbit4all.ui.api.tree.model.TreeNodeKind;
import com.google.common.base.Strings;

public class NavigationViewModelImpl extends ObjectEditingImpl implements NavigationViewModel {

  private TreeModel model;

  private ObservableObjectImpl modelObservable;

  private Navigation navigationState;

  private TreeNode selectedNode;

  private Map<String, TreeNode> treeNodesById;

  public NavigationViewModelImpl(Navigation navigation) {
    this.navigationState = navigation;
    ref = new ApiObjectRef(null, new TreeModel(),
        NavigationViewModelHelper.getNavigationDescriptors());
    modelObservable = new ObservableObjectImpl();
    modelObservable.setRef(ref);
    model = ref.getWrapper(TreeModel.class);
    treeNodesById = new HashMap<>();
    navigationState.subscribeForNodeRefresh(this::refreshNavigationNode);
    navigationState.subscribeForRootNodeAdded(this::rootNodeAdded);
    navigationState.subscribeForRootNodeRemoved(this::rootNodeRemoved);
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
      node.setNavigationTarget(getViewCommand(selectedNode));
    }
  }

  @Override
  public void refreshSelectedNode() {
    if (selectedNode != null) {
      if (selectedNode.getKind() == TreeNodeKind.ASSOCIATION) {
        navigationState.forceRefreshAssociation(selectedNode.getIdentifier());
        NavigationAssociation assoc = navigationState.getAssociation(selectedNode.getIdentifier());
        if (assoc != null) {
          navigationState.expandAll(assoc, true);
          refreshNavigationAssociation(assoc);
        } else {
          throw new RuntimeException("Assoc not found: " + selectedNode);
        }
      } else if (selectedNode.getKind() == TreeNodeKind.ENTRY) {
        navigationState.forceRefreshEntry(selectedNode.getIdentifier());
        NavigationNode navigationNode = navigationState.getNode(selectedNode.getIdentifier());
        if (navigationNode != null) {
          navigationState.refreshNavigationEntry(navigationNode);
          navigationState.expandAll(navigationNode, true);
          refreshNavigationNode(navigationNode);
        } else {
          throw new RuntimeException("Node not found: " + selectedNode);
        }
      }
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
    TreeNode treeNode = treeNodesById.get(node.getId());
    if (treeNode == null) {
      treeNode = new TreeNode()
          .kind(TreeNodeKind.ENTRY)
          .identifier(node.getId())
          .caption(node.getEntry().getName())
          .icon(node.getEntry().getIcon())
          .actions(node.getEntry().getActions())
          .level(level);
      treeNodesById.put(treeNode.getIdentifier(), treeNode);
    } else {
      updateTreeNodeEntry(treeNode, node);
      treeNode.setLevel(level);
    }
    return treeNode;
  }

  private TreeNode createOrUpdateTreeNodeAssoc(NavigationAssociation navigationAssociation,
      int level) {
    TreeNode treeNode = treeNodesById.get(navigationAssociation.getId());
    if (treeNode == null) {
      treeNode = new TreeNode()
          .kind(TreeNodeKind.ASSOCIATION)
          .identifier(navigationAssociation.getId())
          .caption(getAssociationNodeCaption(navigationAssociation))
          .icon(navigationAssociation.getIcon())
          .styles(getNavigationAssociationStyles(navigationAssociation))
          .level(level);
      treeNodesById.put(treeNode.getIdentifier(), treeNode);
    } else {
      updateTreeNodeAssoc(treeNode, navigationAssociation);
      treeNode.setLevel(level);
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
  public  NavigationTarget loadNavigationTarget() {
    if (selectedNode != null) {
      return selectedNode.getNavigationTarget();
    }
    
    return null;
  }
}
