package org.smartbit4all.api.view.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.uitree.bean.UiTreeNode;
import org.smartbit4all.api.uitree.bean.UiTreeState;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class TreeSetupApiImpl implements TreeSetupApi, InitializingBean {

  @Autowired(required = false)
  private List<TreeConfig> treeConfigs;

  @Autowired(required = false)
  private List<TreeNodeRenderer> treeNodeRenderers;

  @Autowired(required = false)
  private List<TreeNodeActionHandler> treeNodeActionHandlers;

  @Autowired(required = false)
  private List<TreeRelation> treeRelations;

  private Map<String, TreeConfig> configsByName = new HashMap<>();
  private Map<String, TreeNodeRenderer> rendererByType = new HashMap<>();
  private Map<String, List<TreeNodeActionHandler>> actionHandlersByType = new HashMap<>();
  private Map<String, TreeRelation> relationsByName = new HashMap<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    if (treeConfigs != null) {
      for (TreeConfig config : treeConfigs) {
        String name = config.getName();
        if (configsByName.containsKey(name)) {
          throw new IllegalArgumentException(
              "TreeConfig " + name + " already registered");
        }
        configsByName.put(name, config);
      }
    }
    if (treeNodeRenderers != null) {
      for (TreeNodeRenderer renderer : treeNodeRenderers) {
        for (String nodeType : renderer.getSupportedNodeTypes()) {
          if (rendererByType.containsKey(nodeType)) {
            throw new IllegalArgumentException(
                "NodeType " + nodeType + " already has a renderer");
          }
          rendererByType.put(nodeType, renderer);
        }
      }
      rendererByType.putIfAbsent(TreeRelation.CONFIG_NODE_TYPE, getConfigNodeRenderer());
    }
    if (treeNodeActionHandlers != null) {
      for (TreeNodeActionHandler actionHandler : treeNodeActionHandlers) {
        for (String nodeType : actionHandler.getSupportedNodeTypes()) {
          actionHandlersByType
              .computeIfAbsent(nodeType, key -> new ArrayList<>())
              .add(actionHandler);
        }
      }
    }
    if (treeRelations != null) {
      for (TreeRelation relation : treeRelations) {
        String name = relation.getName();
        if (relationsByName.containsKey(name)) {
          throw new IllegalArgumentException(
              "Relation " + name + " already registered");
        }
        relationsByName.put(name, relation);
      }
    }
  }

  @Override
  public TreeConfig getTreeConfig(String configName) {
    return configsByName.get(configName);
  }

  @Override
  public TreeNodeRenderer getTreeNodeRenderer(String nodeType) {
    return rendererByType.get(nodeType);
  }

  @Override
  public List<TreeNodeActionHandler> getTreeNodeActionHandlers(String nodeType) {
    List<TreeNodeActionHandler> list = actionHandlersByType.get(nodeType);
    return list == null ? Collections.emptyList() : list;
  }

  @Override
  public TreeRelation getTreeRelation(String relationName) {
    return relationsByName.get(relationName);
  }

  protected TreeNodeRenderer getConfigNodeRenderer() {
    return new TreeNodeRenderer() {

      @Override
      public UiTreeNode renderNode(UiTreeState treeState, String nodeType, ObjectNode object,
          UiTreeNode template) {
        return template;
      }

      @Override
      public List<String> getSupportedNodeTypes() {
        return Arrays.asList(TreeRelation.CONFIG_NODE_TYPE);
      }
    };
  }
}
