package org.smartbit4all.api.view.tree;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.smartbit4all.api.uitree.bean.SmartTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeDefaultSelection;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreePath;
import org.smartbit4all.api.uitree.bean.UiTreePathPart;
import org.smartbit4all.api.uitree.bean.UiTreeState;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class TreeApiImpl implements TreeApi {

  // FIXME (viewApi is not present everywhere)
  @Autowired(required = false)
  private ViewApi viewApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private TreeSetupApi treeSetupApi;

  @Override
  public List<SmartTreeNode> getRootNodes(UiTreeState treeState) {
    List<String> rootNodes = treeState.getRootNodes();
    if (rootNodes.isEmpty()) {
      expandNodeInternal(treeState, getConfigNode(treeState).getIdentifier());
      // refreshNode(treeState, getConfigNode(treeState));
      rootNodes = treeState.getRootNodes();

      final UiTreeDefaultSelection defaultSelection = treeState.getDefaultSelection();
      if (defaultSelection != null) {
        this.setSelectedNode(treeState,
            defaultSelection.getPath(),
            defaultSelection.getHandleSelection());
        if (Boolean.TRUE.equals(defaultSelection.getOneTimeOnly())) {
          treeState.setDefaultSelection(null);
        }
      }
    }
    return getTreeNodeListFromState(treeState, rootNodes);
  }

  @Override
  public List<SmartTreeNode> getChildrenNodes(UiTreeState treeState, String nodeId) {
    UiTreeNode node = getTreeNode(treeState, nodeId);
    if (Boolean.TRUE == node.getHasChildren()) {
      if (node.getChildren().isEmpty()) {
        refreshNode(treeState, node);
      }
      return getTreeNodeListFromState(treeState, node.getChildren());
    }
    return Collections.emptyList();
  }

  @Override
  public SmartTreeNode expandNode(UiTreeState treeState, String nodeId) {
    UiTreeNode treeNode = expandNodeInternal(treeState, nodeId);
    return convertUi2SmartTreeNode(treeState, treeNode);
  }

  private UiTreeNode expandNodeInternal(UiTreeState treeState, String nodeId) {
    UiTreeNode treeNode = getTreeNode(treeState, nodeId);
    if (!treeState.getExpandedNodes().contains(nodeId)) {
      treeState.getExpandedNodes().add(nodeId);
      refreshNode(treeState, treeNode);
    }
    return treeNode;
  }

  @Override
  public SmartTreeNode collapseNode(UiTreeState treeState, String nodeId) {
    UiTreeNode treeNode = getTreeNode(treeState, nodeId);
    if (treeState.getExpandedNodes().contains(nodeId)) {
      treeState.getExpandedNodes().remove(nodeId);
      clearTreeNodeChildren(treeState, treeNode);
    }
    return convertUi2SmartTreeNode(treeState, treeNode);
  }

  @Override
  public SmartTreeNode selectNode(UiTreeState treeState, String nodeId) {
    UiTreeNode treeNode = selectNodeInternal(treeState, nodeId, true);
    return convertUi2SmartTreeNode(treeState, treeNode);
  }

  private UiTreeNode selectNodeInternal(UiTreeState treeState, String nodeId,
      boolean handleSelection) {
    UiTreeNode treeNode = getTreeNode(treeState, nodeId);
    if (!treeState.getSelectedNodes().contains(nodeId)) {
      treeState.getSelectedNodes().clear();
      treeState.getSelectedNodes().add(nodeId);
    }
    if (handleSelection) {
      getTreeConfig(treeState).handleNodeSelected(treeState, treeNode);
    }
    return treeNode;
  }

  @Override
  public UiTreeNode setSelectedNode(UiTreeState treeState, UiTreePath path,
      boolean handleSelection) {
    // get config node, navigate from there
    UiTreeNode node = getConfigNode(treeState);
    // copy path so parameter.parts won't change
    UiTreePath pathCopy = new UiTreePath()
        .parts(new ArrayList<>(path.getParts()));
    UiTreeNode lastChild = navigateToChild(treeState, node, pathCopy);
    if (lastChild != null) {
      selectNodeInternal(treeState, lastChild.getIdentifier(), handleSelection);
    }
    return lastChild;
  }

  private UiTreeNode navigateToChild(UiTreeState treeState, UiTreeNode node, UiTreePath path) {
    if (path.getParts().isEmpty()) {
      return node;
    }
    List<UiTreePathPart> parts = path.getParts();
    UiTreePathPart part = parts.get(0);
    URI objectUri = objectApi.getLatestUri(part.getObjectUri());
    String nodeType = part.getNodeType();
    if (objectUri == null && Strings.isNullOrEmpty(nodeType)) {
      throw new IllegalArgumentException(
          "objectUri and/or nodeType must be specified for navigation");
    }
    // this node is on the path, expand it
    expandNodeInternal(treeState, node.getIdentifier());
    UiTreeNode child = node.getChildren().stream()
        .map(id -> treeState.getNodes().get(id))
        .filter(n -> {
          boolean uri = objectUri == null || objectUri.equals(n.getObjectUri());
          boolean type = nodeType == null || nodeType.equals(n.getNodeType());
          return uri && type;
        })
        .findFirst()
        .orElse(null);
    // TODO if child == null -> try to refresh node and search for child again
    if (child != null) {
      path.setParts(parts.subList(1, parts.size()));
      return navigateToChild(treeState, child, path);
    }
    return null;
  }

  @Override
  public List<SmartTreeNode> performAction(UiTreeState treeState, String nodeId,
      UiActionRequest action) {
    // main actions don't have nodes
    UiTreeNode treeNode = null;
    if (Strings.isNullOrEmpty(nodeId)) {
      List<String> mainActionCodes = getMainActions(treeState)
          .stream()
          .map(UiAction::getCode)
          .collect(toList());
      if (!mainActionCodes.contains(action.getCode())) {
        throw new IllegalArgumentException("TreeNode not found by identifier!");
      }
    } else {
      treeNode = treeState.getNodes().get(nodeId);
      if (treeNode == null) {
        throw new IllegalArgumentException("TreeNode not found by identifier!");
      }
    }
    if (action == null || Strings.isNullOrEmpty(action.getCode())) {
      throw new IllegalArgumentException("Action must be specified!");
    }
    performAction(treeState, treeNode, action);
    return getRootNodes(treeState);
  }

  @Override
  public List<UiAction> getMainActions(UiTreeState treeState) {
    return getConfigNode(treeState).getActions();
  }

  private TreeConfig getTreeConfig(UiTreeState treeState) {
    TreeConfig config = treeSetupApi.getTreeConfig(treeState.getConfig());
    if (config == null) {
      throw new IllegalStateException(
          "TreeConfig not present for configuration " + treeState.getConfig());
    }
    return config;
  }

  private UiTreeNode getConfigNode(UiTreeState treeState) {
    return getTreeConfig(treeState).getConfigNode(treeState);
  }

  private void performAction(UiTreeState treeState, UiTreeNode treeNode, UiActionRequest action) {
    String nodeType = treeNode == null ? null : treeNode.getNodeType();
    String actionCode = action.getCode();
    TreeConfig config = getTreeConfig(treeState);
    if (config.isActionSupported(nodeType, actionCode)) {
      config.performAction(this, treeState, treeNode, action);
    }
  }

  private UiTreeNode getTreeNode(UiTreeState treeState, String element) {
    Objects.requireNonNull(element, "element cannot be null");
    UiTreeNode treeNode = treeState.getNodes().get(element);
    if (treeNode == null) {
      throw new IllegalArgumentException("TreeNode not found by identifier");
    }
    if (!Objects.equals(treeNode.getIdentifier(), element)) {
      throw new IllegalArgumentException("TreeNode identifier is corrupted");
    }
    return treeNode;
  }

  private List<SmartTreeNode> getTreeNodeListFromState(UiTreeState treeState, List<String> ids) {
    return ids.stream()
        .map(id -> treeState.getNodes().get(id))
        .map(n -> convertUi2SmartTreeNode(treeState, n))
        .collect(toList());
  }

  private SmartTreeNode convertUi2SmartTreeNode(UiTreeState treeState, UiTreeNode treeNode) {
    return new SmartTreeNode()
        .identifier(treeNode.getIdentifier())
        .caption(treeNode.getCaption())
        .icon(treeNode.getIcon())
        .selected(treeState.getSelectedNodes().contains(treeNode.getIdentifier()))
        .expanded(treeState.getExpandedNodes().contains(treeNode.getIdentifier()))
        .level(treeNode.getLevel())
        .classes(treeNode.getClasses())
        .actions(treeNode.getActions())
        .hasChildren(treeNode.getHasChildren())
        .childrenNodes(treeNode.getChildren().stream()
            .map(c -> convertUi2SmartTreeNode(treeState, treeState.getNodes().get(c)))
            .collect(toList()));
  }

  @Override
  public void refreshNode(UiTreeState treeState, UiTreeNode node) {
    List<UiTreeNode> childrenNodes =
        getTreeConfig(treeState)
            .readChildrenNodes(treeState, node);

    if (!node.getChildren().isEmpty()) {
      // merge with existing nodes
      Map<URI, String> idByUri = node.getChildren().stream()
          .map(id -> treeState.getNodes().get(id))
          .collect(toMap(
              UiTreeNode::getObjectUri,
              UiTreeNode::getIdentifier));
      for (UiTreeNode child : childrenNodes) {
        String existingId = idByUri.get(child.getObjectUri());
        if (existingId != null) {
          child.identifier(existingId)
              .children(treeState.getNodes().get(existingId).getChildren());

          // TODO parentIdentifier???
        }
      }
    }
    addNodesToState(treeState, childrenNodes);
    node.setChildren(childrenNodes.stream()
        .map(UiTreeNode::getIdentifier)
        .collect(toList()));
    node.setHasChildren(!childrenNodes.isEmpty());
    if (TreeRelation.CONFIG_NODE_TYPE.equals(node.getNodeType())) {
      // root nodes should be refreshed
      treeState.rootNodes(node.getChildren());
    }
    // node.actions(getTreeConfig(treeState).)
    if (node.getObjectUri() != null) {
      ObjectNode object = objectApi.loadLatest(node.getObjectUri());
      List<UiAction> actions =
          getTreeConfig(treeState).getActionsForNode(treeState, node.getNodeType(), object);
      node.setActions(actions);
    }

  }

  @Override
  public void refreshNodeRecursively(UiTreeState treeState, UiTreeNode node) {
    this.refreshNode(treeState, node);
    node.getChildren()
        .stream()
        .map(id -> treeState.getNodes().get(id))
        .filter(child -> Objects.nonNull(child))
        .forEach(child -> refreshNodeRecursively(treeState, child));
  }


  private void addNodesToState(UiTreeState treeState, List<UiTreeNode> nodes) {
    treeState.getNodes().putAll(nodes.stream()
        .collect(Collectors.toMap(
            UiTreeNode::getIdentifier,
            n -> n)));
  }

  private void clearTreeNodeChildren(UiTreeState treeState, UiTreeNode treeNode) {
    for (String id : treeNode.getChildren()) {
      UiTreeNode child = treeState.getNodes().get(id);
      clearTreeNodeChildren(treeState, child);
      treeState.getNodes().remove(id);
    }
    treeState.getExpandedNodes().removeAll(treeNode.getChildren());
    treeState.getSelectedNodes().removeAll(treeNode.getChildren());
    treeNode.getChildren().clear();
  }

  @Override
  public <T> T executeTreeCall(UUID viewUuid, String treeId, Function<UiTreeState, T> treeCall) {
    UiTreeState treeState = viewApi.getComponentModelFromView(UiTreeState.class, viewUuid, treeId);
    treeState.setViewUuid(viewUuid);
    T result = treeCall.apply(treeState);
    viewApi.setComponentModelInView(UiTreeState.class, viewUuid, treeId, treeState);
    return result;
  }

}
