package org.smartbit4all.ui.common.form2.impl;

import java.net.URI;
import java.util.List;
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

  public void selectWidget(URI descriptorUri);

  public void loadAvailableWidgets();

  public void loadTemplate();

  /**
   * Returns the currently available widgets, that are to be presented on the lower part of the
   * screen.
   * 
   * @return the list of {@link WidgetDescriptor} objects, that represent the available choices
   */
  public List<WidgetDescriptor> getAvailableWidgets();

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

  void jumpToStart();

//  public List<PredictiveInputGraphNode> getAvailableNodes();

}
