package org.smartbit4all.api.view;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.uitree.bean.SmartTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeState;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class TreeApiImpl extends PrimaryApiImpl<TreeContributionApi> implements TreeApi {

  // FIXME
  @Autowired(required = false)
  private ViewApi viewApi;

  @Autowired
  private ObjectApi objectApi;

  public TreeApiImpl() {
    super(TreeContributionApi.class);
  }

  @Override
  public void addRootNodesToTree(UiTreeState tree, List<UiTreeNode> roots) {
    addNodesToState(tree, roots);
    tree.getRootNodes().addAll(roots.stream()
        .map(UiTreeNode::getIdentifier)
        .collect(toList()));
  }

  @Override
  public List<SmartTreeNode> getRootNodes(UiTreeState treeState) {
    List<String> rootNodes = treeState.getRootNodes();
    if (rootNodes.isEmpty()) {
      List<UiTreeNode> roots = apiByName.values().stream()
          .flatMap(api -> api.readRootNodes(treeState).stream())
          .collect(toList());
      addRootNodesToTree(treeState, roots);
    }
    return getTreeNodeListFromState(rootNodes, treeState);
  }

  @Override
  public List<SmartTreeNode> getChildrenNodes(UiTreeState treeState, String nodeId) {
    UiTreeNode node = getTreeNode(treeState, nodeId);
    if (Boolean.TRUE == node.getHasChildren()) {
      if (node.getChildren().isEmpty()) {
        populateChildrenOfTreeNode(treeState, node);
      }
      return getTreeNodeListFromState(node.getChildren(), treeState);
    }
    return Collections.emptyList();
  }

  @Override
  public SmartTreeNode expandNode(UiTreeState treeState, String nodeId) {
    UiTreeNode treeNode = getTreeNode(treeState, nodeId);
    if (!treeState.getExpandedNodes().contains(nodeId)) {
      treeState.getExpandedNodes().add(nodeId);
      populateChildrenOfTreeNode(treeState, treeNode);
    }
    return convertUi2SmartTreeNode(treeNode, treeState);
  }

  @Override
  public SmartTreeNode collapseNode(UiTreeState treeState, String nodeId) {
    UiTreeNode treeNode = getTreeNode(treeState, nodeId);
    if (treeState.getExpandedNodes().contains(nodeId)) {
      treeState.getExpandedNodes().remove(nodeId);
      clearTreeNodeChildren(treeState, treeNode);
    }
    return convertUi2SmartTreeNode(treeNode, treeState);
  }

  @Override
  public SmartTreeNode selectNode(UiTreeState treeState, String nodeId) {
    UiTreeNode treeNode = getTreeNode(treeState, nodeId);
    if (!treeState.getSelectedNodes().contains(nodeId)) {
      treeState.getSelectedNodes().clear();
      treeState.getSelectedNodes().add(nodeId);
    }
    handleNodeSelected(treeNode);
    return convertUi2SmartTreeNode(treeNode, treeState);
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
    return apiByName.values().stream()
        .flatMap(api -> api.getMainActions(treeState).stream())
        .collect(toList());
  }

  private List<UiTreeNode> readChildrenNodes(UiTreeNode treeNode) {
    TreeContributionApi api = getContributionApi(treeNode);
    if (api != null) {
      return api.readChildrenNodes(treeNode);
    }
    return Collections.emptyList();
  }

  private void handleNodeSelected(UiTreeNode treeNode) {
    TreeContributionApi api = getContributionApi(treeNode);
    if (api != null) {
      api.handleNodeSelected(treeNode);
    }
  }

  private TreeContributionApi getContributionApi(UiTreeNode treeNode) {
    TreeContributionApi contributionApi = null;
    String nodeType = treeNode == null ? null : treeNode.getNodeType();
    for (TreeContributionApi api : apiByName.values()) {
      if (api.isNodeTypeSupported(nodeType)) {
        contributionApi = api;
        break;
      }
    }
    return contributionApi;
  }

  private void performAction(UiTreeState treeState, UiTreeNode treeNode, UiActionRequest action) {
    String nodeType = treeNode == null ? null : treeNode.getNodeType();
    String actionCode = action.getCode();
    for (TreeContributionApi api : apiByName.values()) {
      if (api.isActionSupported(nodeType, actionCode)) {
        api.performAction(this, treeState, treeNode, action);
        break;
      }
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

  private List<SmartTreeNode> getTreeNodeListFromState(List<String> ids, UiTreeState treeState) {
    return ids.stream()
        .map(id -> treeState.getNodes().get(id))
        .map(n -> convertUi2SmartTreeNode(n, treeState))
        .collect(toList());
  }

  private SmartTreeNode convertUi2SmartTreeNode(UiTreeNode treeNode, UiTreeState treeState) {
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
            .map(c -> convertUi2SmartTreeNode(treeState.getNodes().get(c), treeState))
            .collect(toList()));
  }

  @Override
  public void populateChildrenOfTreeNode(UiTreeState treeState, UiTreeNode node) {
    List<UiTreeNode> childrenNodes = readChildrenNodes(node);
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
          child.setIdentifier(existingId);
          // TODO parentIdentifier???
        }
      }
    }
    addNodesToState(treeState, childrenNodes);
    node.setChildren(childrenNodes.stream()
        .map(UiTreeNode::getIdentifier)
        .collect(toList()));
    node.setHasChildren(!childrenNodes.isEmpty());
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
    UiTreeState treeState = getTreeState(viewUuid, treeId);
    T result = treeCall.apply(treeState);
    applyTreeState(viewUuid, treeId, treeState);
    return result;
  }

  private UiTreeState getTreeState(UUID viewUuid, String treeId) {
    String[] paths = getPathParts(treeId);
    String location = paths[0];
    String key = paths[1];
    if (View.PARAMETERS.equals(location)) {
      View view = viewApi.getView(viewUuid);
      Object treeStateObject = view.getParameters().get(key);
      return objectApi.asType(UiTreeState.class, treeStateObject);
    } else if (View.MODEL.equals(location)) {
      Object model = viewApi.getModel(viewUuid, null);
      return objectApi.getValueFromObject(UiTreeState.class, model, key);
    } else {
      throw new IllegalArgumentException("Invalid path");
    }
    // return getModel(viewUuid).getTree();
  }

  private void applyTreeState(UUID viewUuid, String treeId, UiTreeState treeState) {
    String[] paths = getPathParts(treeId);
    String location = paths[0];
    String key = paths[1];
    if (View.PARAMETERS.equals(location)) {
      View view = viewApi.getView(viewUuid);
      view.putParametersItem(key, treeState);
    } else if (View.MODEL.equals(location)) {
      Object model = viewApi.getModel(viewUuid, null);
      Map<String, Object> modelAsMap = objectApi.definition(model.getClass()).toMap(model);
      modelAsMap.put(key, objectApi.definition(treeState.getClass()).toMap(treeState));
      Object updatedModel = objectApi.asType(model.getClass(), modelAsMap);
      viewApi.getView(viewUuid).setModel(updatedModel);
    } else {
      throw new IllegalArgumentException("Invalid path");
    }
  }

  private String[] getPathParts(String treeId) {
    String[] paths = treeId.split("\\.");
    if (paths == null) {
      throw new IllegalArgumentException("Empty path not allowed");
    }
    if (paths.length != 2) {
      throw new IllegalArgumentException("Invalid path");
    }
    return paths;
  }


}
