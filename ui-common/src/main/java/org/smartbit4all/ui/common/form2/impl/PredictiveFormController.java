package org.smartbit4all.ui.common.form2.impl;

import java.net.URI;
import java.util.List;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;

public interface PredictiveFormController {

  /**
   * Sets the {@link PredictiveFormInstanceView} of the controller, so the render methods can be
   * called.
   * 
   * @param ui the {@link PredictiveFormInstanceView} to be set in the controller
   */
  public void setUI(PredictiveFormInstanceView ui);

  public void stepBack();

  public void save();

  public void loadAvailableWidgets();

  public void loadTemplate(URI uri);

  /**
   * Returns the currently visible widgets, that are to be presented on the upper part of the
   * screen, with the corresponding data as well.
   * 
   * @return the list of {@link WidgetInstance} objects, that represent the already selected and
   *         filled widgets
   */
  public List<WidgetInstance> getVisibleWidgets();

  public WidgetDescriptor getWidgetDescriptor(URI descriptorUri);

  public void saveWidgetInstance(WidgetInstance instance);

  public void goToRoot();

  public List<PredictiveInputGraphNode> getAvailableNodes();

  public void selectWidget(PredictiveInputGraphNode node);

}
