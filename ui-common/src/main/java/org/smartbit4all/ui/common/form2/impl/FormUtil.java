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

  // TODO This class should have better and more relevant mappings, which would make the code cleaner
  private Map<URI, WidgetDescriptor> widgetDescriptorsByUris;
  private Map<URI, WidgetInstance> widgetInstancesByUris;
  private Map<URI, PredictiveInputGraphNode> nodesByUris;
  private Map<WidgetDescriptor, PredictiveInputGraphNode> nodesByDescriptors;
  
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
    nodesByDescriptors = createNodesByDescriptors();
  }

  private Map<WidgetDescriptor, PredictiveInputGraphNode> createNodesByDescriptors() {
    Map<WidgetDescriptor, PredictiveInputGraphNode> map = new HashMap<>();
    for (PredictiveInputGraphNode node : nodesByUris.values()) {
      map.put(widgetDescriptorsByUris.get(node.getDescriptorUri()), node);
    }
    return map;
  }

  private void gatherNodesFromGraph(List<PredictiveInputGraphNode> rootNodes) {
    for (PredictiveInputGraphNode node : rootNodes) {
      // TODO at this point dynamically assigned uris would be possible, 
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
  
  public PredictiveInputGraphNode getNode(WidgetDescriptor descriptor) {
    return nodesByDescriptors.get(descriptor);
  }
  
  public void removeWidgetInstanceFromMappings(WidgetInstance instance) {
    widgetInstancesByUris.remove(instance.getDescriptorUri());
  }
  
  public WidgetInstance getParentInstance(WidgetInstance instance) {
    PredictiveInputGraphNode node = getNode(getDescriptor(instance.getDescriptorUri()));
    PredictiveInputGraphNode parentNode = getNode(node.getParent());
    WidgetInstance parentInstance = getInstance(parentNode.getDescriptorUri());
    return parentInstance;
  }

}