package org.smartbit4all.ui.common.form2.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.form.PredictiveFormApi;
import org.smartbit4all.ui.api.form.model.PredictiveFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class PredictiveFormApiImpl implements PredictiveFormApi {

  @Override
  public PredictiveFormInstance loadInstance(URI uri) {
    if (uri.equals(URI.create("instances/mock"))) {
      return createMockInstance();
    } else {
      return null;
    }
  }

  private PredictiveFormInstance createMockInstance() {
    PredictiveFormInstance instance = new PredictiveFormInstance();
    instance.setUri(URI.create("instances/mock"));
    instance.setGraph(MockWidgets.inputGraphDescriptor);
    // TODO maybe this should have a father node, which contains the root node options?
    instance.setActiveNode(instance.getGraph().getRootNodes().get(0));
    instance.setAvailableWidgets(getAvailableWidgets(instance.getActiveNode()));
    
    return instance;
  }

  /**
   * Gets the available widgets based on the position in the graph.
   * 
   * @param node the currently active node.
   * @return the list of available {@link WidgetDescriptor} items.
   */
  private List<WidgetDescriptor> getAvailableWidgets(PredictiveInputGraphNode node) {
    List<WidgetDescriptor> availableWidgets = new ArrayList<>();
    for (PredictiveInputGraphNode n : node.getChildren()) {
      WidgetDescriptor widgetDescriptor = MockWidgets.descriptorsByUris.get(n.getDescriptorUri());
      availableWidgets.add(widgetDescriptor);
    }
    return availableWidgets;
  }

  @Override
  public void saveForm(PredictiveFormInstance instance) {
    // TODO Auto-generated method stub

  }



}
