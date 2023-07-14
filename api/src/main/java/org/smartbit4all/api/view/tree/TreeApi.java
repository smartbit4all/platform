package org.smartbit4all.api.view.tree;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.smartbit4all.api.uitree.bean.SmartTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreePath;
import org.smartbit4all.api.uitree.bean.UiTreeState;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;

/**
 * This interface is used to serve tree implementations.
 *
 * @author matea
 *
 */
public interface TreeApi {

  List<SmartTreeNode> getRootNodes(UiTreeState treeState);

  List<SmartTreeNode> getChildrenNodes(UiTreeState treeState, String nodeId);

  SmartTreeNode expandNode(UiTreeState treeState, String nodeId);

  SmartTreeNode collapseNode(UiTreeState treeState, String nodeId);

  SmartTreeNode selectNode(UiTreeState treeState, String nodeId);

  /**
   * Finds parent node, (re)expands it (so children nodes are reloaded), and selects child node.
   * Typically used for selecting newly created child node. Call selectNode with handleSelecting =
   * true.
   *
   * @param treeState
   * @param parentNodeId
   * @param childUri
   * @return
   */
  SmartTreeNode selectNodeByUri(UiTreeState treeState, String parentNodeId, URI childUri);

  List<SmartTreeNode> performAction(UiTreeState treeState, String nodeId,
      UiActionRequest action);

  List<UiAction> getMainActions(UiTreeState treeState);

  void refreshNode(UiTreeState treeState, UiTreeNode node);

  // for calling with viewUuid and treeId
  <T> T executeTreeCall(UUID viewUuid, String treeId, Function<UiTreeState, T> treeCall);

  /**
   * Expands and selects tree specified by objectPath. First URI will be searched in rootNodes.
   *
   * @param treeState
   * @param path list of URIs and types which will be used to match nodes.
   * @param handleSelection if true, handleSelect will be called (exactly as in
   *        {@link #selectNode(UiTreeState, String)})
   */
  UiTreeNode setSelectedNode(UiTreeState treeState, UiTreePath path, boolean handleSelection);

  /**
   * Refreshes the state of the provided node and all its opened child nodes (continues downward
   * recursively).
   *
   * <p>
   * Shall be used when an action enacted on a treeNode has some blanket effect(s) on all its
   * children.
   *
   * @param treeState the {@code UiTreeState} serving as the node's context, not null
   * @param node a single {@code UiTreeNode} in the above tree, not null
   */
  void refreshNodeRecursively(UiTreeState treeState, UiTreeNode node);

  /**
   * Refreshes the state of whole tree.
   *
   * @param treeState the {@code UiTreeState} serving as the node's context, not null
   */
  void refreshTreeRecursively(UiTreeState treeState);

}
