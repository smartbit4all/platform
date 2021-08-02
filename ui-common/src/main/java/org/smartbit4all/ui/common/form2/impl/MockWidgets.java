package org.smartbit4all.ui.common.form2.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphDescriptor;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode.KindEnum;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetType;

public class MockWidgets {
  
  public static  Map<URI, WidgetDescriptor> descriptorsByUris = createDescriptorsByUris();
  
  public static PredictiveInputGraphDescriptor inputGraphDescriptor = createInputGraphDescriptor();
  
  private static Map<URI, WidgetDescriptor> createDescriptorsByUris() {
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

  private static PredictiveInputGraphDescriptor createInputGraphDescriptor() {
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
