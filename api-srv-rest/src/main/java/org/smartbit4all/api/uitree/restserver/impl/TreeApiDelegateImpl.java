package org.smartbit4all.api.uitree.restserver.impl;

import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.uitree.bean.SmartTreeNode;
import org.smartbit4all.api.uitree.restserver.TreeApiDelegate;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ViewContextChange;
import org.smartbit4all.api.view.tree.TreeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class TreeApiDelegateImpl implements TreeApiDelegate {

  @Autowired
  private TreeApi treeApi;

  @Autowired
  private ViewContextService viewContextService;

  @Override
  public ResponseEntity<SmartTreeNode> collapseNode(UUID viewUuid, String treeId, String nodeId)
      throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.collapseNode(treeState, nodeId)));
  }

  @Override
  public ResponseEntity<ViewContextChange> collapseNode2(UUID viewUuid, String treeId,
      String nodeId)
      throws Exception {
    return ResponseEntity.ok(
        viewContextService.performViewCall(
            () -> treeApi.executeTreeCall(viewUuid, treeId,
                treeState -> treeApi.collapseNode(treeState, nodeId)),
            "collapseNode"));
  }

  @Override
  public ResponseEntity<SmartTreeNode> expandNode(UUID viewUuid, String treeId, String nodeId)
      throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.expandNode(treeState, nodeId)));
  }

  @Override
  public ResponseEntity<ViewContextChange> expandNode2(UUID viewUuid, String treeId, String nodeId)
      throws Exception {
    return ResponseEntity.ok(
        viewContextService.performViewCall(
            () -> treeApi.executeTreeCall(viewUuid, treeId,
                treeState -> treeApi.expandNode(treeState, nodeId)),
            "expandNode"));
  }

  @Override
  public ResponseEntity<List<SmartTreeNode>> getChildrenNodes(UUID viewUuid, String treeId,
      String nodeId) throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.getChildrenNodes(treeState, nodeId)));
  }

  @Override
  public ResponseEntity<ViewContextChange> getChildrenNodes2(UUID viewUuid, String treeId,
      String nodeId) throws Exception {
    return ResponseEntity.ok(
        viewContextService.performViewCall(
            () -> treeApi.executeTreeCall(viewUuid, treeId,
                treeState -> treeApi.getChildrenNodes(treeState, nodeId)),
            "getChildrenNodes"));
  }

  @Override
  public ResponseEntity<List<UiAction>> getMainActions(UUID viewUuid, String treeId)
      throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.getMainActions(treeState)));
  }

  @Override
  public ResponseEntity<ViewContextChange> getMainActions2(UUID viewUuid, String treeId)
      throws Exception {
    return ResponseEntity.ok(
        viewContextService.performViewCall(
            () -> treeApi.executeTreeCall(viewUuid, treeId,
                treeState -> treeApi.getMainActions(treeState)),
            "getMainActions"));
  }

  @Override
  public ResponseEntity<List<SmartTreeNode>> getRootNodes(UUID viewUuid, String treeId)
      throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.getRootNodes(treeState)));
  }

  @Override
  public ResponseEntity<ViewContextChange> getRootNodes2(UUID viewUuid, String treeId)
      throws Exception {
    return ResponseEntity.ok(
        viewContextService.performViewCall(
            () -> treeApi.executeTreeCall(viewUuid, treeId,
                treeState -> treeApi.getRootNodes(treeState)),
            "getRootNodes"));
  }

  @Override
  public ResponseEntity<List<SmartTreeNode>> performAction(UUID viewUuid, String treeId,
      String nodeId, UiActionRequest request) throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.performAction(treeState, nodeId, request)));
  }

  @Override
  public ResponseEntity<ViewContextChange> performAction2(UUID viewUuid, String treeId,
      String nodeId, UiActionRequest request) throws Exception {
    return ResponseEntity.ok(
        viewContextService.performViewCall(
            () -> treeApi.executeTreeCall(viewUuid, treeId,
                treeState -> treeApi.performAction(treeState, nodeId, request)),
            "performMainAction"));
  }

  @Override
  public ResponseEntity<List<SmartTreeNode>> performMainAction(UUID viewUuid, String treeId,
      UiActionRequest request) throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.performAction(treeState, null, request)));
  }

  @Override
  public ResponseEntity<ViewContextChange> performMainAction2(UUID viewUuid, String treeId,
      UiActionRequest request) throws Exception {
    return ResponseEntity.ok(
        viewContextService.performViewCall(
            () -> treeApi.executeTreeCall(viewUuid, treeId,
                treeState -> treeApi.performAction(treeState, null, request)),
            "performMainAction"));
  }

  @Override
  public ResponseEntity<SmartTreeNode> selectNode(UUID viewUuid, String treeId, String nodeId)
      throws Exception {
    return ResponseEntity.ok(treeApi.executeTreeCall(viewUuid, treeId,
        treeState -> treeApi.selectNode(treeState, nodeId)));
  }

  @Override
  public ResponseEntity<ViewContextChange> selectNode2(UUID viewUuid, String treeId, String nodeId)
      throws Exception {
    return ResponseEntity.ok(
        viewContextService.performViewCall(
            () -> treeApi.executeTreeCall(viewUuid, treeId,
                treeState -> treeApi.selectNode(treeState, nodeId)),
            "selectNode"));
  }
}
