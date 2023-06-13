package org.smartbit4all.api.view.tree;

import java.util.List;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeState;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.core.object.ObjectNode;

/**
 * TreeConfig is a set of {@link TreeRelation}s specified at construct time. Objects of this
 * interface will be used be {@link TreeApi}, when serving trees. TreeConfigs will be searched by
 * {@link UiTreeState#getConfig()}.
 *
 * Apart from relations, actions are handled by TreeConfig (see performAction and
 * handleNodeSelected).
 *
 * TreeConfig will create a configNode in the treeState, which will represent a single, non-visible
 * treeNode with the type {@link TreeRelation#CONFIG_NODE_TYPE}. Relations contributing root nodes
 * will have to set parentNodeType to this value.
 *
 * @author matea
 *
 */
public interface TreeConfig {

  String getName();

  List<UiTreeNode> readChildrenNodes(UiTreeState treeState, UiTreeNode treeNode);

  void handleNodeSelected(UiTreeState treeState, UiTreeNode treeNode);

  boolean isActionSupported(UiTreeState treeState, String nodeType, String actionCode);

  List<UiAction> getActionsForNode(UiTreeState treeState, String nodeType, ObjectNode object);

  void performAction(UiTreeState treeState, UiTreeNode treeNode, UiActionRequest action);

  UiTreeNode getConfigNode(UiTreeState treeState);

}
