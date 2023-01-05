package org.smartbit4all.api.view.tree;

import java.util.List;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeState;
import org.smartbit4all.core.object.ObjectNode;

public interface TreeNodeRenderer {

  List<String> getSupportedNodeTypes();

  UiTreeNode renderNode(UiTreeState treeState, String nodeType, ObjectNode object,
      UiTreeNode template);

}
