package org.smartbit4all.ui.vaadin.components.form2;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.api.form.PredictiveFormApi;
import org.smartbit4all.ui.api.form.model.EntityFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveFormInstance;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.api.form.model.WidgetType;
import org.smartbit4all.ui.common.form2.impl.FormUtil;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormInstanceView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class PredictiveFormControllerImpl implements PredictiveFormController {

  private static final Logger log = LoggerFactory.getLogger(PredictiveFormControllerImpl.class);

  private PredictiveFormApi api;
  private PredictiveFormInstanceView ui;
  private PredictiveFormInstance predictiveFormInstance;
  private EntityFormInstance entityFormInstance;
  private FormUtil util;

  public PredictiveFormControllerImpl(PredictiveFormApi predictiveFormApi) {
    api = predictiveFormApi;
  }

  @Override
  public void setUI(PredictiveFormInstanceView ui) {
    this.ui = ui;
  }

  @Override
  public void stepBack() {
    URI parentUri = predictiveFormInstance.getActiveNode().getParent();
    if (parentUri != null) {
      PredictiveInputGraphNode parentNode = util.getNode(parentUri);
      predictiveFormInstance.setActiveNode(parentNode);
      setAvailableWidgets(parentNode);
      ui.renderWidgets();
    }
  }

  @Override
  public void save() {
    api.saveForm(entityFormInstance);
  }

  private WidgetInstance createWidgetInstanceFromDescriptor(WidgetDescriptor widgetDescriptor) {
    WidgetInstance instance = new WidgetInstance();
    instance.setDescriptorUri(widgetDescriptor.getUri());
    return instance;
  }

  private void setAvailableWidgets(PredictiveInputGraphNode node) {
    predictiveFormInstance.getAvailableWidgets().clear();
    List<PredictiveInputGraphNode> children = util.getChildNodes(node);
    for (PredictiveInputGraphNode n : children) {
      WidgetDescriptor widgetDescriptor = getWidgetDescriptor(n.getDescriptorUri());
      predictiveFormInstance.getAvailableWidgets().add(widgetDescriptor);
    }
  }

  @Override
  public void loadAvailableWidgets() {
    // TODO Auto-generated method stub
  }

  @Override
  public void loadTemplate(URI uri) {
    entityFormInstance = api.loadInstance(uri);
    predictiveFormInstance = entityFormInstance.getPredictiveForm();
    util = new FormUtil(entityFormInstance);

    ui.renderWidgets();
  }

  @Override
  public List<WidgetInstance> getVisibleWidgets() {
    return predictiveFormInstance.getVisibleWidgets();
  }

  @Override
  public WidgetDescriptor getWidgetDescriptor(URI descriptorUri) {
    return util.getDescriptor(descriptorUri);
  }

  @Override
  public void goToRoot() {
    PredictiveInputGraphNode activeNode = predictiveFormInstance.getGraph().getRootNodes().get(0);
    predictiveFormInstance.setActiveNode(activeNode);
    setAvailableWidgets(activeNode);
    ui.renderWidgets();
  }

  @Override
  public List<PredictiveInputGraphNode> getAvailableNodes() {
    List<PredictiveInputGraphNode> nodes = util
        .getChildNodes(predictiveFormInstance.getActiveNode()).stream()
        .filter(n -> util.getInstance(n.getDescriptorUri()) == null).collect(Collectors.toList());

    return nodes;
  }

  @Override
  // TODO this could be improved by adding better, more relevant mappings to the FormUtil
  // so the elements can be accessible easier
  public void addWidget(PredictiveInputGraphNode node) {
    WidgetDescriptor widgetDescriptor = util.getDescriptor(node);
    WidgetType widgetType = widgetDescriptor.getWidgetType();
    // only setting active node if it's a container, otherwise setting nodes would be counterproductive.
    if (widgetType == WidgetType.CONTAINER) {
      predictiveFormInstance.setActiveNode(node);
    }
    
    if (widgetDescriptor != null) {
      WidgetInstance widgetInstance = createWidgetInstanceFromDescriptor(widgetDescriptor);
      // we don't want to open the dialog for containers immediately, as they don't hold any value!
      if (WidgetType.CONTAINER != widgetType) {
        ui.openValueDialog(widgetDescriptor.getWidgetType(), widgetInstance, widgetDescriptor);
      }
      predictiveFormInstance.getAvailableWidgets()
          .removeIf(w -> w.getUri().equals(node.getDescriptorUri()));
      
      WidgetInstance parentWidgetInstance =
          util.getInstance(util.getNode(node.getParent()).getDescriptorUri());
      
      if (parentWidgetInstance != null) {
        parentWidgetInstance.addWidgetsItem(widgetInstance);
        util.addInstanceByUri(widgetInstance.getDescriptorUri(), widgetInstance);
      } else {
        predictiveFormInstance.addVisibleWidgetsItem(widgetInstance);
        util.addInstanceByUri(widgetInstance.getDescriptorUri(), widgetInstance);
      }
      
      predictiveFormInstance.getAvailableWidgets()
          .removeIf(w -> w.getUri().equals(node.getDescriptorUri()));
    }
    ui.renderWidgets();
  }

  @Override
  public void selectWidget(WidgetInstance instance) {
    WidgetDescriptor descriptor = util.getDescriptor(instance.getDescriptorUri());
    PredictiveInputGraphNode node = util.getNode(descriptor);
    predictiveFormInstance.activeNode(node);
    setAvailableWidgets(node);
    ui.renderWidgets();
  }

  @Override
  public boolean isWidgetSelected(WidgetInstance instance) {
    return predictiveFormInstance.getActiveNode().getDescriptorUri()
        .equals(instance.getDescriptorUri());
  }

  @Override
  // TODO this could be improved, some of this functionality should be in FormUtil
  public void deleteWidgetInstance(WidgetInstance instance) {
    PredictiveInputGraphNode node = util.getNode(util.getDescriptor(instance.getDescriptorUri()));
    WidgetInstance parentInstance = util.getParentInstance(instance);
    
    
    if (parentInstance != null) {
      parentInstance.getWidgets().remove(instance);
    } else {
      predictiveFormInstance.getVisibleWidgets().remove(instance);
    }
    
    if (node.getChildren() != null && !node.getChildren().isEmpty()) {
      for (PredictiveInputGraphNode n : node.getChildren()) {
        WidgetInstance instanceToDelete = util.getInstance(n.getDescriptorUri());
        deleteWidgetInstance(instanceToDelete);
      }
    }
    util.removeWidgetInstanceFromMappings(instance);
    ui.renderWidgets();
    
  }
  
  private void deleteContainedWidgets(WidgetInstance instance) {
    if (instance.getWidgets() != null && !instance.getWidgets().isEmpty()) {
      for (WidgetInstance wi : instance.getWidgets()) {
        deleteContainedWidgets(wi);
      }
    }
    util.getParentInstance(instance).getWidgets().remove(instance);
  }
}
