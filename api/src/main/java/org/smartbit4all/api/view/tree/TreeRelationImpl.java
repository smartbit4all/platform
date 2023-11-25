package org.smartbit4all.api.view.tree;

import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.VersionStrategy;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeState;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TreeRelationImpl implements TreeRelation {

  protected final String name;
  protected final String parentNodeType;
  protected final String childNodeType;

  @Autowired
  protected ObjectApi objectApi;

  @Autowired
  protected TreeSetupApi treeSetupApi;

  public TreeRelationImpl(String name, String parentNodeType, String childNodeType) {
    this.name = name;
    this.parentNodeType = parentNodeType;
    this.childNodeType = childNodeType;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getParentNodeType() {
    return parentNodeType;
  }

  @Override
  public List<UiTreeNode> readChildrenNodes(TreeConfig treeConfig, UiTreeState treeState,
      UiTreeNode parentTreeNode) {
    ObjectNode parentNode = readParentObjectNode(treeState, parentTreeNode);
    return readRelatedObjectNodes(treeState, parentNode)
        .map(object -> renderNode(treeConfig, treeState, childNodeType, object,
            parentTreeNode.getIdentifier(), parentTreeNode.getLevel() + 1))
        .collect(toList());
  }

  protected ObjectNode readParentObjectNode(UiTreeState treeState, UiTreeNode parentTreeNode) {
    return objectApi.load(parentTreeNode.getObjectUri());
  }

  // for ex. return parent.list(objectRelation).nodeStream();
  protected abstract Stream<ObjectNode> readRelatedObjectNodes(UiTreeState treeState,
      ObjectNode parent);

  protected UiTreeNode renderNode(TreeConfig treeConfig, UiTreeState treeState, String nodeType,
      ObjectNode object,
      String parentId, int level) {
    TreeNodeRenderer renderer = treeSetupApi.getTreeNodeRenderer(nodeType);
    if (renderer == null) {
      throw new IllegalArgumentException(
          "Relation " + name + " doesn't support rendering " + nodeType);
    }
    List<UiAction> actions = treeConfig.getActionsForNode(treeState, nodeType, object);
    UiTreeNode template = new UiTreeNode()
        .identifier(UUID.randomUUID().toString())
        .parentIdentifier(parentId)
        .caption(nodeType)
        .level(level)
        .hasChildren(false)
        .objectUri(VersionStrategy.EXACT.equals(treeConfig.versionStrategy(treeState))
            ? object.getObjectUri()
            : objectApi.getLatestUri(object.getObjectUri()))
        .nodeType(nodeType)
        .actions(actions);
    return renderer.renderNode(treeState, nodeType, object, template);
  }

}
