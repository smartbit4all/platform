package org.smartbit4all.ui.vaadin.components.form2;

import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.common.form2.impl.PredictiveFormController;
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
  
  /**
   * The predictive form UI instance.
   */
  private PredictiveFormInstanceView ui;
  
  public PredictiveFormControllerImpl() {
    ui = new PredictiveFormInstanceView(this);
  }

  /**
   * Steps back into the parent node of the current node. <br>
   * <b> SHOULD HAVE THE NODES AS PARAMETERS</b>
   */
  @Override
  public void stepBack() {
    // TODO body and signature
  }

  /**
   * Loads the root (or roots) of the graph, showing the available widgets on the uppermost level.
   * <br>
   * <b> SHOULD HAVE SOME PARAMETERS</b>
   */
  @Override
  public void loadRoot() {
    // TODO body and signature
  }


  /**
   * Saves the data from the visible widgets and the state of the form and nodes.
   */
  @Override
  public void save() {
    // TODO body and signature
  }

  @Override
  public void selectWidget(WidgetDescriptor descriptor) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void loadAvailableWidgets() {
    // TODO Auto-generated method stub
    
  }

}
