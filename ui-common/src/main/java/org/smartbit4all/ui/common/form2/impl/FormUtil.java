package org.smartbit4all.ui.common.form2.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.ui.api.form.model.EntityFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;

public class FormUtil {
  
  private static Map<URI, WidgetDescriptor> widgetDescriptorsByUris = createwidgetDescriptorsByUris();
  private static Map<PredictiveInputGraphNode, WidgetDescriptor> widgetDescriptorsByNodes = new HashMap<>();
  private static Map<URI, WidgetInstance> widgetInstancesByUris = new HashMap<>();
  
  public static WidgetDescriptor getDescriptor(URI uri) {
    return widgetDescriptorsByUris.get(uri);
  }
  
  private static Map<URI, WidgetDescriptor> createwidgetDescriptorsByUris() {
    Map<URI, WidgetDescriptor> map = new HashMap<>();
    MockWidgets mockWidgets = new MockWidgets();
    map = mockWidgets.descriptorsByUris;
    return map;
  }

  public static WidgetDescriptor getDescriptor(PredictiveInputGraphNode node) {
    return widgetDescriptorsByNodes.get(node);
  }
  
  public static WidgetInstance getInstance(URI uri) {
    return widgetInstancesByUris.get(uri);
  }
  
  public static EntityFormInstance loadInstance(URI uri) {
    // TODO this is where we have to load the instance, based on the given URI.
    return null;
  }
  
  

}
