package org.smartbit4all.ui.common.form2.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.ui.api.form.model.EntityFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;

public class FormUtil {

  private Map<URI, WidgetDescriptor> widgetDescriptorsByUris;
  private Map<URI, WidgetInstance> widgetInstancesByUris;
  private Map<URI, PredictiveInputGraphNode> nodesByUris;
  private EntityFormInstance entityFormInstance;

  public FormUtil(EntityFormInstance entityFormInstance) {
    this.entityFormInstance = entityFormInstance;
    initMaps();
  }

  private void initMaps() {
    widgetDescriptorsByUris = createwidgetDescriptorsByUris();
    createNodesByUris();
    widgetInstancesByUris = new HashMap<>();
  }

  private void createNodesByUris() {
    nodesByUris = new HashMap<>();
    gatherNodesFromGraph(entityFormInstance.getPredictiveForm().getGraph().getRootNodes());
  }

  private void gatherNodesFromGraph(List<PredictiveInputGraphNode> rootNodes) {
    for (PredictiveInputGraphNode node : rootNodes) {
      // TODO at this point dinamically assigned uris would be possible, 
      // but right now we're working with the statically assigned uris
      nodesByUris.put(node.getUri(), node);
      if (node.getChildren() != null && !node.getChildren().isEmpty()) {
        gatherNodesFromGraph(node.getChildren());
      }
    }
  }

  public WidgetDescriptor getDescriptor(URI uri) {
    return widgetDescriptorsByUris.get(uri);
  }

  private Map<URI, WidgetDescriptor> createwidgetDescriptorsByUris() {
    Map<URI, WidgetDescriptor> map = new HashMap<>();
    for (WidgetDescriptor widget : entityFormInstance.getWidgets()) {
      map.put(widget.getUri(), widget);
    }
    return map;
  }

  public WidgetDescriptor getDescriptor(PredictiveInputGraphNode node) {
    return widgetDescriptorsByUris.get(node.getDescriptorUri());
  }

  public WidgetInstance getInstance(URI uri) {
    return widgetInstancesByUris.get(uri);
  }

  public void addInstanceByUri(URI uri, WidgetInstance instance) {
    if (widgetInstancesByUris == null) {
      widgetInstancesByUris = new HashMap<>();
    }
    widgetInstancesByUris.put(uri, instance);
  }

  public List<PredictiveInputGraphNode> getChildNodes(PredictiveInputGraphNode activeNode) {
    List<PredictiveInputGraphNode> nodeList = new ArrayList<>();
    for (PredictiveInputGraphNode node : activeNode.getChildren()) {
      nodeList.add(node);
    }
    return nodeList;
  }

  public PredictiveInputGraphNode getNode(URI uri) {
    return nodesByUris.get(uri);
  }

}
