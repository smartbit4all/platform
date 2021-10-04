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
import org.smartbit4all.api.navigation.NavigationApi;
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

  private NavigationApi navigationApi;

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
      } else if (selectedNode.getKind() == TreeNodeKind.ENTRY) {
        navigationState.forceRefreshEntry(selectedNode.getIdentifier());
      }
      NavigationNode navigationNode = navigationState.getNode(selectedNode.getIdentifier());
      if (navigationNode != null) {
        refreshNavigationNode(navigationNode);
      } else {
        for (TreeNode root : model.getRootNodes()) {
          navigationNode = navigationState.getNode(root.getIdentifier());
          if (navigationNode != null) {
            refreshNavigationNode(navigationNode);
          }
        }
      }
    }
  }

  private void loadChildren(TreeNode parent) {
    if (!parent.getChildrenNodesLoaded()) {
      parent.getChildrenNodes().clear();
      // Kind kind, String identifier, String caption, String shortDescription,
      // String icon, String[] styles
      if (parent.getKind() == TreeNodeKind.ASSOCIATION) {
        navigationState.getReferencedNodes(parent.getIdentifier())
            .stream()
            .map(n -> treeNodeOf(n, parent.getLevel() + 1))
            .forEachOrdered(n -> parent.getChildrenNodes().add(n));
      } else {

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
              .map(navNode -> new TreeNode()
                  .kind(TreeNodeKind.ENTRY)
                  .identifier(navNode.getId())
                  .caption(navNode.getEntry().getName())
                  .icon(navNode.getEntry().getIcon())
                  .actions(navNode.getEntry().getActions())
                  .level(parent.getLevel() + 1))
              .collect(Collectors.toList());
          treeNodes.forEach(treeNode -> treeNodesById.put(treeNode.getIdentifier(), treeNode));
          treeNodesByOrderedAssocIds.put(assocId, treeNodes);
        });


        // TODO Correct name for the association
        associations.stream()
            .filter(a -> !a.getHidden())
            .forEach(navigationAssociation -> {
              List<String> styles = new ArrayList<>();
              if (navigationAssociation.getReferences() == null
                  || navigationAssociation.getReferences().isEmpty()) {
                styles.add("empty");
              }

              TreeNode treeNode = new TreeNode()
                  .kind(TreeNodeKind.ASSOCIATION)
                  .identifier(navigationAssociation.getId())
                  .caption(getAssociationNodeCaption(navigationAssociation))
                  .icon(navigationAssociation.getIcon())
                  .styles(styles)
                  .level(parent.getLevel() + 1);
              treeNodesById.put(treeNode.getIdentifier(), treeNode);
              // TreeNode treeNode = new TreeNode(
              // Kind.ASSOCIATION,
              // navigationAssociation.getId(),
              // getAssociationNodeCaption(navigationAssociation),
              // null,
              // navigationAssociation.getIcon(),
              // styles,
              // null);

              treeNodesByOrderedAssocIds.put(navigationAssociation.getId(),
                  Collections.singletonList(treeNode));
            });


        treeNodesByOrderedAssocIds.forEach((assoc, nodes) -> {
          if (nodes != null) {
            parent.getChildrenNodes().addAll(nodes);
          }
        });
      }

      parent.setChildrenNodesLoaded(Boolean.TRUE);
    }
    parent.setHasChildren(parent.getChildrenNodes().size() > 0);
  }

  private TreeNode treeNodeOf(NavigationNode node, int level) {
    TreeNode treeNode = new TreeNode()
        .kind(TreeNodeKind.ENTRY)
        .identifier(node.getId())
        .caption(node.getEntry().getName())
        .icon(node.getEntry().getIcon())
        .actions(node.getEntry().getActions())
        .level(level);
    treeNodesById.put(treeNode.getIdentifier(), treeNode);
    return treeNode;
  }

  private void updateTreeNode(TreeNode treeNode, NavigationNode node) {
    treeNode
        .kind(TreeNodeKind.ENTRY)
        .identifier(node.getId())
        .caption(node.getEntry().getName())
        .icon(node.getEntry().getIcon())
        .actions(node.getEntry().getActions());
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

  private void refreshNavigationNode(NavigationNode navigationNode) {

    TreeNode nodeToRefresh = findTreeNodeById(navigationNode.getId());
    if (nodeToRefresh != null) {
      updateTreeNode(nodeToRefresh, navigationNode);
      nodeToRefresh.setChildrenNodesLoaded(false);
      loadChildren(nodeToRefresh);
      notifyAllListeners();
    }
  }

  private void rootNodeAdded(NavigationNode rootNode) {
    TreeNode treeNode = treeNodeOf(rootNode, 0);
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
}
