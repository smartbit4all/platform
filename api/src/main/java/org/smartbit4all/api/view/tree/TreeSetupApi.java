package org.smartbit4all.api.view.tree;

import java.util.List;

/**
 * This API is responsible for collecting all tree related configurations.
 *
 * @author matea
 *
 */
public interface TreeSetupApi {

  TreeConfig getTreeConfig(String configName);

  TreeNodeRenderer getTreeNodeRenderer(String nodeType);

  List<TreeNodeActionHandler> getTreeNodeActionHandlers(String nodeType);

  TreeRelation getTreeRelation(String relationName);

}
