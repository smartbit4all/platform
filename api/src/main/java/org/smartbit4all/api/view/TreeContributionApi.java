package org.smartbit4all.api.view;

import java.util.List;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeState;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface TreeContributionApi extends ContributionApi {

  boolean isNodeTypeSupported(String nodeType);

  List<UiAction> getMainActions(UiTreeState treeState);

  List<UiTreeNode> readRootNodes(UiTreeState treeState);

  List<UiTreeNode> readChildrenNodes(UiTreeNode treeNode);

  void handleNodeSelected(UiTreeNode treeNode);

  boolean isActionSupported(String nodeType, String actionCode);

  void performAction(TreeApi treeApi, UiTreeState treeState, UiTreeNode node,
      UiActionRequest action);

}
