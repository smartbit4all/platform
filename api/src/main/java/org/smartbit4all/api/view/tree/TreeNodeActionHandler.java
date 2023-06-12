package org.smartbit4all.api.view.tree;

import java.util.List;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeState;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface TreeNodeActionHandler {

  List<String> getSupportedNodeTypes();

  boolean isActionSupported(UiTreeState treeState, String nodeType, String actionCode);

  void performAction(UiTreeState treeState, UiTreeNode node, UiActionRequest action);

}
