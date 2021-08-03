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
    nodesByUris = createNodesByUris();
    widgetInstancesByUris = new HashMap<>();
  }

  private Map<URI, PredictiveInputGraphNode> createNodesByUris() {
    Map<URI, PredictiveInputGraphNode> map = new HashMap<>();
    List<PredictiveInputGraphNode> nodes = entityFormInstance.getPredictiveForm().getGraph().getNodes();
    for (PredictiveInputGraphNode node : nodes) {
      map.put(node.getUri(), node);
    }
    return map;
  }

  public WidgetDescriptor getDescriptor(URI uri) {
    return widgetDescriptorsByUris.get(uri);
  }
  
  private Map<URI, WidgetDescriptor> createwidgetDescriptorsByUris() {
    Map<URI, WidgetDescriptor> map = new HashMap<>();
    for (WidgetDescriptor  widget : entityFormInstance.getWidgets()) {
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
    for (URI uri : activeNode.getChildren()) {
      nodeList.add(nodesByUris.get(uri));
    }
    return nodeList;
  }
  
  public PredictiveInputGraphNode getNode(URI uri) {
    return nodesByUris.get(uri);
  }

}
