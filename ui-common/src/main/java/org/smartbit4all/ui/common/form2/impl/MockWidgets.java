package org.smartbit4all.ui.common.form2.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.ui.api.form.model.EntityFormDescriptor;
import org.smartbit4all.ui.api.form.model.EntityFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphDescriptor;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode.KindEnum;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetType;

public class MockWidgets {
  
  public List<WidgetDescriptor> descriptorList;
  public PredictiveInputGraphDescriptor inputGraphDescriptor = createInputGraphDescriptor();
  public Map<PredictiveInputGraphNode, WidgetDescriptor> descriptorsByNodes;
  public Map<URI, PredictiveInputGraphNode> nodesByUris;
  
  public MockWidgets() {
    descriptorList = createWidgetDescriptors();
    int i = 0;
    for (WidgetDescriptor descriptor : descriptorList) {
      descriptor.setUri(createDescriptorUri(i++));
    }
  }  
  
  // TODO in the mock version an instance is created, and an entityFormDescriptor is added.
  // This should be reversed, the instance should be created from the descriptor.
  public EntityFormInstance createMockEntityFormInstance() {
    EntityFormInstance instance = new EntityFormInstance();
    instance.setPredictiveForm(createMockPredictiveFormInstance());
    EntityFormDescriptor descriptor = createEntityFormDescriptor();
    instance.setDescriptorUri(URI.create("demoform/entitydescriptor/1"));
    instance.setUri(URI.create("health-fs/instances/entities/1"));
    instance.setWidgets(descriptor.getWidgets());
    return instance;
  }

  private EntityFormDescriptor createEntityFormDescriptor() {
    EntityFormDescriptor descriptor = new EntityFormDescriptor();
    descriptor.setPredictiveLayout(inputGraphDescriptor);
    descriptor.setWidgets(descriptorList);
    descriptor.setUri(URI.create("demoform/entitydescriptor/1"));
    return descriptor;
  }

  private List<WidgetDescriptor> createWidgetDescriptors() {
    List<WidgetDescriptor> descriptorList = new ArrayList<>();
    WidgetDescriptor root = new WidgetDescriptor().label("").icon("user").widgetType(WidgetType.CONTAINER);
    WidgetDescriptor personalData = new WidgetDescriptor().label("Személyes adatok").icon("user").widgetType(WidgetType.CONTAINER);
    WidgetDescriptor healthData = new WidgetDescriptor().label("Egészségügyi adatok").icon("clipboard-cross").widgetType(WidgetType.CONTAINER);
    WidgetDescriptor temp = new WidgetDescriptor().label("Testhőmérséklet").icon("spline-area-chart").widgetType(WidgetType.INTEGER);
    WidgetDescriptor lastName = new WidgetDescriptor().label("Vezetéknév").icon("arrow-right").widgetType(WidgetType.TEXT_INTERVAL);
    WidgetDescriptor firstName = new WidgetDescriptor().label("Keresztnév").icon("arrow-left").widgetType(WidgetType.TEXT);
    descriptorList.add(root);
    descriptorList.add(personalData);
    descriptorList.add(healthData);
    descriptorList.add(temp);
    descriptorList.add(lastName);
    descriptorList.add(firstName);
    return descriptorList;
  }
  
  
  private PredictiveFormInstance createMockPredictiveFormInstance() {
    PredictiveFormInstance instance = new PredictiveFormInstance();
    instance.setUri(URI.create("instances/mock"));
    instance.setGraph(inputGraphDescriptor);
    instance.setActiveNode(instance.getGraph().getRootNodes().get(0));
    instance.setAvailableWidgets(getAvailableWidgets(instance.getActiveNode()));
    
    return instance;
  }

  private List<WidgetDescriptor> getAvailableWidgets(PredictiveInputGraphNode node) {
    List<WidgetDescriptor> availableWidgets = new ArrayList<>();
    for (PredictiveInputGraphNode n : node.getChildren()) {
//      PredictiveInputGraphNode n = nodesByUris.get(u);
      WidgetDescriptor widgetDescriptor = descriptorList.stream().filter(d -> d.getUri().equals(n.getDescriptorUri())).findFirst().orElse(null);
      if (widgetDescriptor != null) {
        availableWidgets.add(widgetDescriptor);
      }
    }
    return availableWidgets;
  }

  private PredictiveInputGraphDescriptor createInputGraphDescriptor() {
    PredictiveInputGraphDescriptor graph = new PredictiveInputGraphDescriptor();
    graph.setRootNodes(createRootNodes());
//    graph.setNodes(nodesByUris.values().stream().collect(Collectors.toList()));
    return graph;
  }
  
  // root
  private List<PredictiveInputGraphNode> createRootNodes() {
    List<PredictiveInputGraphNode> rootNodes = new ArrayList<>();
    
    PredictiveInputGraphNode root = new PredictiveInputGraphNode();
    root.setKind(KindEnum.WIDGET);
    root.setMultiplicity(1);
    root.setDescriptorUri(createDescriptorUri(0));
    URI uri = createNodeUri(0);
    nodesByUris = new HashMap<>();
    nodesByUris.put(uri, root);
    root.setUri(uri);
    root.addChildrenItem(createFirstChild().parent(uri));
    root.addChildrenItem(createSecondChild().parent(uri));
    rootNodes.add(root);
    
    return rootNodes;
  }

  // Személyes adatok
  private PredictiveInputGraphNode createFirstChild() {
    PredictiveInputGraphNode node = new PredictiveInputGraphNode();
    node.setKind(KindEnum.WIDGET);
    node.setDescriptorUri(createDescriptorUri(1));
    node.setMultiplicity(1);
    URI uri = createNodeUri(1);
    nodesByUris.put(uri, node);
    node.setUri(uri);
    node.addChildrenItem(createFirstNameNode().parent(uri));
    node.addChildrenItem(createLastNameNode().parent(uri));
    return node;
  }

  private PredictiveInputGraphNode createLastNameNode() {
    PredictiveInputGraphNode node = new PredictiveInputGraphNode();
    node.setKind(KindEnum.WIDGET);
    node.setDescriptorUri(createDescriptorUri(4));
    URI uri = createNodeUri(4);
    node.setUri(uri);
    nodesByUris.put(uri, node);
    node.setMultiplicity(1);
    return node;
  }

  private PredictiveInputGraphNode createFirstNameNode() {
    PredictiveInputGraphNode node = new PredictiveInputGraphNode();
    node.setKind(KindEnum.WIDGET);
    node.setDescriptorUri(createDescriptorUri(5));
    URI uri = createNodeUri(5);
    node.setUri(uri);
    nodesByUris.put(uri, node);
    node.setMultiplicity(1);
    return node;
  }

  // EÜ adatok
  private PredictiveInputGraphNode createSecondChild() {
    PredictiveInputGraphNode node = new PredictiveInputGraphNode();
    node.setKind(KindEnum.WIDGET);
    node.setDescriptorUri(createDescriptorUri(2));
    node.setMultiplicity(1);
    URI uri = createNodeUri(2);
    nodesByUris.put(uri, node);
    node.setUri(uri);
    node.addChildrenItem(createTemperatureNode().parent(uri));
    return node;
  }

  private PredictiveInputGraphNode createTemperatureNode() {
    PredictiveInputGraphNode node = new PredictiveInputGraphNode();
    node.setKind(KindEnum.WIDGET);
    node.setDescriptorUri(createDescriptorUri(3));
    URI uri = createNodeUri(3);
    node.setUri(uri);
    nodesByUris.put(uri, node);
    node.setMultiplicity(1);
    return node;
  }
  
  private URI createDescriptorUri(int position) {
    return URI.create("widgets/" + position);
  }
  
  private URI createNodeUri(int position) {
    return URI.create("nodes/" + position);
  }
  

}
