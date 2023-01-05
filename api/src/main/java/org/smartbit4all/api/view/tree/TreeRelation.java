package org.smartbit4all.api.view.tree;

import java.util.List;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeState;

public interface TreeRelation {

  public static final String CONFIG_NODE_TYPE = "TreeRelation.configNodeType";

  String getName();

  String getParentNodeType();

  List<UiTreeNode> readChildrenNodes(TreeConfig treeConfig, UiTreeState treeState,
      UiTreeNode treeNode);

}
