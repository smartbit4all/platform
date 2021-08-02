package org.smartbit4all.ui.vaadin.components.form2;

import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.api.form.PredictiveFormApi;
import org.smartbit4all.ui.api.form.model.PredictiveFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.api.form.model.WidgetType;
import org.smartbit4all.ui.common.form2.impl.MockWidgets;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormInstanceView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * The controller of the PredictiveForm.
 * 
 * @author Zsombor Nyilas
 */
@Service
@Scope("prototype")
public class PredictiveFormControllerImpl implements PredictiveFormController {

  private static final Logger log = LoggerFactory.getLogger(PredictiveFormControllerImpl.class);

  private PredictiveFormApi api;

  /**
   * The predictive form UI instance.
   */
  private PredictiveFormInstanceView ui;

  /**
   * The model, representing the predictive form instance itself.
   */
  private PredictiveFormInstance formInstance;
  
  private WidgetInstance currentContainer;
  
  private WidgetInstance currentWidget;

  public PredictiveFormControllerImpl(PredictiveFormApi predictiveFormApi) {
    api = predictiveFormApi;
    formInstance = api.loadInstance(URI.create("instances/mock"));
    currentContainer = null;
    currentWidget = null;
  }

  @Override
  public void setUI(PredictiveFormInstanceView ui) {
    this.ui = ui;
  }

  @Override
  public void stepBack() {
    // TODO body and signature
  }

  @Override
  public void save() {
    api.saveForm(formInstance);
  }

  @Override
  public void selectWidget(URI descriptorUri) {
    // TODO this won't be from mockwidgets in the long run
    WidgetDescriptor widgetDescriptor = MockWidgets.descriptorsByUris.get(descriptorUri);
    if (widgetDescriptor != null) {
      WidgetInstance widgetInstance = createWidgetInstanceFromDescriptor(widgetDescriptor);
      ui.openValueDialog(widgetDescriptor.getWidgetType(), widgetInstance);
      
      if (currentWidget == null) {
        formInstance.addVisibleWidgetsItem(widgetInstance);
      } else {
        currentWidget.addWidgetsItem(widgetInstance);
      }

      currentWidget = widgetInstance;
      formInstance.getAvailableWidgets().removeIf(w -> w.getUri().equals(descriptorUri));
      
      // no need to change active node, if its a leaf
      if (widgetDescriptor.getWidgetType() == WidgetType.CONTAINER) {
        PredictiveInputGraphNode nextNode = formInstance.getActiveNode().getChildren().stream()
            .filter(n -> n.getDescriptorUri().equals(descriptorUri)).findFirst().orElse(null);
        if (nextNode != null) {
          formInstance.setActiveNode(nextNode);
          setAvailableWidgets(nextNode);
//          currentContainer = widgetInstance;
        }
      }
      
      ui.renderWidgets();
    }
  }

  private WidgetInstance createWidgetInstanceFromDescriptor(WidgetDescriptor widgetDescriptor) {
    WidgetInstance instance = new WidgetInstance();
    instance.setDescriptorUri(widgetDescriptor.getUri());
    return instance;
  }
  
  private void setAvailableWidgets(PredictiveInputGraphNode node) {
    formInstance.getAvailableWidgets().clear();
    List<PredictiveInputGraphNode> children = node.getChildren();
    for (PredictiveInputGraphNode n : children) {
      WidgetDescriptor widgetDescriptor = getWidgetDescriptor(n.getDescriptorUri());
      formInstance.getAvailableWidgets().add(widgetDescriptor);
    }
  }

  @Override
  public void loadAvailableWidgets() {
    // TODO Auto-generated method stub
  }

  @Override
  public void loadTemplate() {
    // TODO Auto-generated method stub
  }

  @Override
  public List<WidgetDescriptor> getAvailableWidgets() {
    return formInstance.getAvailableWidgets();
  }

  @Override
  public List<WidgetInstance> getVisibleWidgets() {
    return formInstance.getVisibleWidgets();
  }

  @Override
  public WidgetDescriptor getWidgetDescriptor(URI descriptorUri) {
    return MockWidgets.descriptorsByUris.get(descriptorUri);
  }

  @Override
  public void saveWidgetInstance(WidgetInstance instance) {
    // TODO Auto-generated method stub

  }
  
  @Override
  public void jumpToStart() {
    PredictiveInputGraphNode activeNode = formInstance.getGraph().getRootNodes().get(0);
    formInstance.setActiveNode(activeNode);
    setAvailableWidgets(activeNode);
    ui.renderWidgets();
  }

}
