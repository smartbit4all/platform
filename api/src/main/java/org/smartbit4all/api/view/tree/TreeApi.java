package org.smartbit4all.api.view.tree;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.smartbit4all.api.uitree.bean.SmartTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
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

  List<SmartTreeNode> performAction(UiTreeState treeState, String nodeId,
      UiActionRequest action);

  List<UiAction> getMainActions(UiTreeState treeState);

  void refreshNode(UiTreeState treeState, UiTreeNode node);

  // for calling with viewUuid and treeId
  <T> T executeTreeCall(UUID viewUuid, String treeId, Function<UiTreeState, T> treeCall);

}
