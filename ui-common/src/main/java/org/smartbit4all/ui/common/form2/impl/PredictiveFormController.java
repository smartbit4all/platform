package org.smartbit4all.ui.common.form2.impl;

import java.net.URI;
import java.util.List;
import org.smartbit4all.ui.api.form.model.EntityFormInstance;
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

  /**
   * Sets the parent node of the currently active node as the active node in the descriptor graph,
   * and renders the widgets according to this.
   */
  public void stepBack();

  /**
   * Saves the EntityFormInstance.
   */
  public void save();

  /**
   * Loads the available widgets, so they can be seen on the lower part of the screen.
   */
  public void loadAvailableWidgets();

  /**
   * Loads the EntityFormInstance based on the given URI.
   * 
   * @param uri the uri pointing to an existing {@link EntityFormInstance}
   */
  public void loadTemplate(URI uri);

  /**
   * Returns the currently visible widgets, that are to be presented on the upper part of the
   * screen, with the corresponding data as well.
   * 
   * @return the list of {@link WidgetInstance} objects, that represent the already selected and
   *         filled widgets
   */
  public List<WidgetInstance> getVisibleWidgets();

  /**
   * Returns the {@link WidgetDescriptor} object corresponding to the given URI.
   * 
   * @param descriptorUri the URI of a {@link WidgetDescriptor}
   * @return the correct {@link WidgetDescriptor} object.
   */
  public WidgetDescriptor getWidgetDescriptor(URI descriptorUri);

  /**
   * Sets the root node in the input graph as the currently active node, and renders the widgets
   * accordingly.
   */
  public void goToRoot();

  /**
   * Returns the list of the currently available {@link PredictiveInputGraphNode} objects.
   * 
   * @return the currently available {@link PredictiveInputGraphNode} list
   */
  public List<PredictiveInputGraphNode> getAvailableNodes();

  /**
   * Adds the selected widget from the available widgets to the visible widgets, based on the graph
   * node that contains the descriptor.
   * 
   * @param node the {@link PredictiveInputGraphNode} that contains the added widgets descriptor.
   */
  public void addWidget(PredictiveInputGraphNode node);

  /**
   * Selects a {@link WidgetInstance} object, based on the users mouse click, and sets the active
   * graph node as well.
   * 
   * @param instance the selected {@link WidgetInstance}
   */
  public void selectWidget(WidgetInstance instance);

  /**
   * Returns a boolean which represent whether the given instance is selected or not.
   * 
   * @param instance the {@link WidgetInstance} whose active state is in question
   * @return boolean representing whether or not the instance is the active one.
   */
  public boolean isWidgetSelected(WidgetInstance instance);

  /**
   * Removes a widget instance from the screen and the inner mappings, and renders the widgets
   * accordingly.
   * 
   * @param instance the instance to be removed.
   */
  public void deleteWidgetInstance(WidgetInstance instance);

}
