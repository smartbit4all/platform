package org.smartbit4all.api.uitree.restserver.impl;

import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.uitree.bean.SmartTreeNode;
import org.smartbit4all.api.uitree.restserver.TreeApiDelegate;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.tree.TreeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class TreeApiDelegateImpl implements TreeApiDelegate {

  @Autowired
  private TreeApi treeApi;

  @Override
  public ResponseEntity<SmartTreeNode> collapseNode(UUID viewUuid, String treeId, String nodeId)
      throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.collapseNode(treeState, nodeId)));
  }

  @Override
  public ResponseEntity<SmartTreeNode> expandNode(UUID viewUuid, String treeId, String nodeId)
      throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.expandNode(treeState, nodeId)));
  }

  @Override
  public ResponseEntity<List<SmartTreeNode>> getChildrenNodes(UUID viewUuid, String treeId,
      String nodeId) throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.getChildrenNodes(treeState, nodeId)));
  }

  @Override
  public ResponseEntity<List<UiAction>> getMainActions(UUID viewUuid, String treeId)
      throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.getMainActions(treeState)));
  }

  @Override
  public ResponseEntity<List<SmartTreeNode>> getRootNodes(UUID viewUuid, String treeId)
      throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.getRootNodes(treeState)));
  }

  @Override
  public ResponseEntity<List<SmartTreeNode>> performAction(UUID viewUuid, String treeId,
      String nodeId, UiActionRequest body) throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.performAction(treeState, nodeId, body)));
  }

  @Override
  public ResponseEntity<List<SmartTreeNode>> performMainAction(UUID viewUuid, String treeId,
      UiActionRequest body) throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.performAction(treeState, null, body)));
  }

  @Override
  public ResponseEntity<SmartTreeNode> selectNode(UUID viewUuid, String treeId, String nodeId)
      throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.selectNode(treeState, nodeId)));
  }

}
