package org.smartbit4all.api.view.tree;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeState;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TreeConfigImpl implements TreeConfig {

  protected final String configName;

  /**
   * lazy initialized, use getRelations() to be on the safe side
   */
  protected Map<String, TreeRelation> relations;

  protected final List<String> relationNames;

  @Autowired
  protected TreeSetupApi treeSetupApi;

  public TreeConfigImpl(String configName, List<String> relationNames) {
    this.configName = configName;
    this.relationNames = relationNames;
  }

  @Override
  public String getName() {
    return configName;
  }

  @Override
  public UiTreeNode getConfigNode(UiTreeState treeState) {
    UiTreeNode configNode;
    if (treeState.getNodes().containsKey(configName)) {
      configNode = treeState.getNodes().get(configName);
    } else {
      configNode = createConfigNode(treeState);
    }
    return configNode;
  }

  protected UiTreeNode createConfigNode(UiTreeState treeState) {
    UiTreeNode configNode;
    List<UiAction> actions = getActionsForNode(treeState, configName, null);
    configNode = new UiTreeNode()
        .identifier(configName)
        .parentIdentifier(null)
        .caption(configName)
        .level(-1)
        .hasChildren(true)
        .objectUri(null)
        .nodeType(TreeRelation.CONFIG_NODE_TYPE)
        .actions(actions);
    TreeNodeRenderer renderer = treeSetupApi.getTreeNodeRenderer(configName);
    if (renderer != null) {
      configNode = renderer.renderNode(treeState, configName, null, configNode);
    }
    treeState.getNodes().put(configNode.getIdentifier(), configNode);
    return configNode;
  }

  @Override
  public List<UiTreeNode> readChildrenNodes(UiTreeState treeState, UiTreeNode treeNode) {
    String nodeType = treeNode.getNodeType();
    return getRelations().values().stream()
        .filter(rel -> nodeType.equals(rel.getParentNodeType()))
        .flatMap(rel -> rel.readChildrenNodes(this, treeState, treeNode).stream())
        .map(node -> node.actions(getActionsForNode(treeState, node.getNodeType(), null)))
        .collect(toList());
  }

  private Map<String, TreeRelation> getRelations() {
    if (relations == null) {
      initRelations();
    }
    return relations;
  }

  // lazy initializations by name
  private synchronized void initRelations() {
    relations = relationNames.stream()
        .collect(toMap(
            relationName -> relationName,
            relationName -> treeSetupApi.getTreeRelation(relationName)));
  }

  @Override
  public boolean isActionSupported(String nodeType, String actionCode) {
    String type = nodeType == null ? configName : nodeType;
    return treeSetupApi.getTreeNodeActionHandlers(type)
        .stream()
        .anyMatch(handler -> handler.isActionSupported(type, actionCode));
  }

  @Override
  public void performAction(TreeApi treeApi, UiTreeState treeState, UiTreeNode treeNode,
      UiActionRequest action) {
    UiTreeNode node = treeNode == null ? getConfigNode(treeState) : treeNode;
    String type = treeNode == null ? configName : node.getNodeType();
    treeSetupApi.getTreeNodeActionHandlers(type)
        .stream()
        .filter(handler -> handler.isActionSupported(type, action.getCode()))
        .findAny()
        .ifPresent(handler -> handler.performAction(treeApi, treeState, node, action));
  }

}
