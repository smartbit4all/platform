package org.smartbit4all.ui.common.form2.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.ui.api.form.model.EntityFormDescriptor;
import org.smartbit4all.ui.api.form.model.EntityFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphDescriptor;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode.KindEnum;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetType;

public class MockWidgets {
  
  public Map<URI, WidgetDescriptor> descriptorsByUris = createDescriptorsByUris();
  public PredictiveInputGraphDescriptor inputGraphDescriptor = createInputGraphDescriptor();
  
  public MockWidgets() {
    descriptorsByUris = createDescriptorsByUris();
  }  
  
  public EntityFormInstance createMockEntityFormInstance() {
    EntityFormInstance instance = new EntityFormInstance();
    instance.setPredictiveForm(createMockPredictiveFormInstance());
    EntityFormDescriptor descriptor = createEntityFormDescriptor();
    instance.setDescriptorUri(URI.create("demoform/entitydescriptor/1"));
    instance.setUri(URI.create("health-fs/instances/entities/1"));
    return instance;
  }

  private EntityFormDescriptor createEntityFormDescriptor() {
    EntityFormDescriptor descriptor = new EntityFormDescriptor();
    descriptor.setPredictiveLayout(inputGraphDescriptor);
    descriptor.setWidgets(descriptorsByUris.values().stream().collect(Collectors.toList()));
    descriptor.setUri(URI.create("demoform/entitydescriptor/1"));
    return descriptor;
  }

  private Map<URI, WidgetDescriptor> createDescriptorsByUris() {
    WidgetDescriptor root = new WidgetDescriptor().label("").icon("user").widgetType(WidgetType.CONTAINER).uri(URI.create("demofrom/descriptors/1"));
    WidgetDescriptor personalData = new WidgetDescriptor().label("Személyes adatok").icon("user").widgetType(WidgetType.CONTAINER).uri(URI.create("demofrom/descriptors/2"));
    WidgetDescriptor healthData = new WidgetDescriptor().label("Egészségügyi adatok").icon("clipboard-cross").widgetType(WidgetType.CONTAINER).uri(URI.create("demofrom/descriptors/3"));
    WidgetDescriptor temp = new WidgetDescriptor().label("Testhőmérséklet").icon("spline_area_chart").widgetType(WidgetType.TEXT).uri(URI.create("demofrom/descriptors/4"));
    WidgetDescriptor lastName = new WidgetDescriptor().label("Vezetéknév").icon("arrow_right").widgetType(WidgetType.TEXT).uri(URI.create("demofrom/descriptors/5"));
    WidgetDescriptor firstName = new WidgetDescriptor().label("Keresztnév").icon("arrow_left").widgetType(WidgetType.TEXT).uri(URI.create("demofrom/descriptors/6"));
    
    
    Map<URI, WidgetDescriptor> map = new HashMap<>();
    map.put(URI.create("demofrom/descriptors/1"), root);
    map.put(URI.create("demofrom/descriptors/2"), personalData);
    map.put(URI.create("demofrom/descriptors/3"), healthData);
    map.put(URI.create("demofrom/descriptors/4"), temp);
    map.put(URI.create("demofrom/descriptors/5"), lastName);
    map.put(URI.create("demofrom/descriptors/6"), firstName);
    return map;
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
      WidgetDescriptor widgetDescriptor = descriptorsByUris.get(n.getDescriptorUri());
      availableWidgets.add(widgetDescriptor);
    }
    return availableWidgets;
  }

  private PredictiveInputGraphDescriptor createInputGraphDescriptor() {
    PredictiveInputGraphDescriptor graph = new PredictiveInputGraphDescriptor();
    graph.setRootNodes(createRootNodes());
    
    return graph;
  }
  
  // root
  private static List<PredictiveInputGraphNode> createRootNodes() {
    List<PredictiveInputGraphNode> rootNodes = new ArrayList<>();
    
    PredictiveInputGraphNode root = new PredictiveInputGraphNode();
    root.setKind(KindEnum.WIDGET);
    root.setMultiplicity(1);
    root.setDescriptorUri(createRootDescriptorUri());
    root.addChildrenItem(createFirstChild());
    root.addChildrenItem(createSecondChild());
    rootNodes.add(root);
    
    return rootNodes;
  }
  
  private static URI createRootDescriptorUri() {
    return URI.create("demofrom/descriptors/1");
  }

  // Személyes adatok
  private static PredictiveInputGraphNode createFirstChild() {
    PredictiveInputGraphNode node = new PredictiveInputGraphNode();
    node.setKind(KindEnum.WIDGET);
    node.setDescriptorUri(createFirstChildDescriptorUri());
    node.setMultiplicity(1);
    node.addChildrenItem(createFirstNameNode());
    node.addChildrenItem(createLastNameNode());
    return node;
  }

  private static PredictiveInputGraphNode createLastNameNode() {
    PredictiveInputGraphNode node = new PredictiveInputGraphNode();
    node.setKind(KindEnum.WIDGET);
    node.setDescriptorUri(createLastNameUri());
    node.setMultiplicity(1);
    return node;
  }

  private static URI createLastNameUri() {
    return URI.create("demofrom/descriptors/5");
  }

  private static PredictiveInputGraphNode createFirstNameNode() {
    PredictiveInputGraphNode node = new PredictiveInputGraphNode();
    node.setKind(KindEnum.WIDGET);
    node.setDescriptorUri(createFirstNameUri());
    node.setMultiplicity(1);
    return node;
  }

  private static URI createFirstNameUri() {
    return URI.create("demofrom/descriptors/6");
  }

  private static URI createFirstChildDescriptorUri() {
    return URI.create("demofrom/descriptors/2");
  }

  // EÜ adatok
  private static PredictiveInputGraphNode createSecondChild() {
    PredictiveInputGraphNode node = new PredictiveInputGraphNode();
    node.setKind(KindEnum.WIDGET);
    node.setDescriptorUri(createSecondChildDescriptorUri());
    node.setMultiplicity(1);
    node.addChildrenItem(createTemperatureNode());
    return node;
  }

  private static PredictiveInputGraphNode createTemperatureNode() {
    PredictiveInputGraphNode node = new PredictiveInputGraphNode();
    node.setKind(KindEnum.WIDGET);
    node.setDescriptorUri(createTempUri());
    node.setMultiplicity(1);
    return node;
  }

  private static URI createTempUri() {
    return URI.create("demofrom/descriptors/4");
  }

  private static URI createSecondChildDescriptorUri() {
    return URI.create("demofrom/descriptors/3");
  }
  

}
