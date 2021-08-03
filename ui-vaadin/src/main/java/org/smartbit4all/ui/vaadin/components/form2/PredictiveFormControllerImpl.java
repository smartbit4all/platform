package org.smartbit4all.ui.vaadin.components.form2;

import java.net.URI;
import java.util.List;
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

/**
 * The controller of the PredictiveForm.
 * 
 * @author Zsombor Nyilas
 */
@Service
@Scope("prototype")
public class PredictiveFormControllerImpl implements PredictiveFormController {

  // private static final Logger log = LoggerFactory.getLogger(PredictiveFormControllerImpl.class);

  private PredictiveFormApi api;
  private PredictiveFormInstanceView ui;
  private PredictiveFormInstance predictiveFormInstance;
  private EntityFormInstance entityFormInstance;

  public PredictiveFormControllerImpl(PredictiveFormApi predictiveFormApi) {
    api = predictiveFormApi;
  }

  @Override
  public void setUI(PredictiveFormInstanceView ui) {
    this.ui = ui;
  }

  @Override
  public void stepBack() {
    // TODO body
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
    List<PredictiveInputGraphNode> children = node.getChildren();
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
    ui.renderWidgets();
  }

  // @Override
  public List<WidgetDescriptor> getAvailableWidgets() {
    return predictiveFormInstance.getAvailableWidgets();
  }

  @Override
  public List<WidgetInstance> getVisibleWidgets() {
    return predictiveFormInstance.getVisibleWidgets();
  }

  @Override
  public WidgetDescriptor getWidgetDescriptor(URI descriptorUri) {
    return FormUtil.getDescriptor(descriptorUri);
//    return null;
  }

  @Override
  public void saveWidgetInstance(WidgetInstance instance) {
    // TODO Auto-generated method stub

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
    return predictiveFormInstance.getActiveNode().getChildren();
  }


  // TODO maybe this should be called addWidget or something more specific, cause a select method will
  // be needed for the mouse click selection too
  @Override
  public void selectWidget(PredictiveInputGraphNode node) {
    WidgetDescriptor widgetDescriptor = FormUtil.getDescriptor(node);
    if (widgetDescriptor.getWidgetType() == WidgetType.CONTAINER) {
      predictiveFormInstance.setActiveNode(node);
    }
    if (widgetDescriptor != null) {
      WidgetInstance widgetInstance = createWidgetInstanceFromDescriptor(widgetDescriptor);
      ui.openValueDialog(widgetDescriptor.getWidgetType(), widgetInstance);
      predictiveFormInstance.getAvailableWidgets().removeIf(w -> w.getUri().equals(node.getDescriptorUri()));
      // WidgetInstance parentWidgetInstance = formInstance.getVisibleWidgets().stream()
      // .filter(w -> w.getDescriptorUri().equals(node.getParent().getDescriptorUri())).findFirst()
      // .orElse(null); // formutils.getwidgetinstancebyuri
      WidgetInstance parentWidgetInstance =
          FormUtil.getInstance(node.getParent().getDescriptorUri());
      if (parentWidgetInstance != null) {
        parentWidgetInstance.addWidgetsItem(widgetInstance);
      } else {
        predictiveFormInstance.addVisibleWidgetsItem(widgetInstance);
      }
      predictiveFormInstance.getAvailableWidgets().removeIf(w -> w.getUri().equals(node.getDescriptorUri()));
    }
    ui.renderWidgets();
  }
}
