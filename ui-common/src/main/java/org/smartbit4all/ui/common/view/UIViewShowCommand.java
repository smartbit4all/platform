package org.smartbit4all.ui.common.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.ui.common.controller.UIController;

/**
 * This instance is created by the {@link UIController} if the UI should show a view and set some
 * parameters for the new view. From the controller perspective it doesn't matter if it's a new view
 * or an already existing one.
 * 
 * @author Peter Boros
 */
public class UIViewShowCommand {

  private String viewName;

  /**
   * Parameters (because of the limited capabilities of the Vaadin QueryParameter) it's a map of
   * values.
   */
  private Map<String, List<String>> parameters;

  public UIViewShowCommand(String viewName) {
    super();
    this.viewName = viewName;
    parameters = new HashMap<>();
  }

  public final String getViewName() {
    return viewName;
  }

  public final void setViewName(String viewName) {
    this.viewName = viewName;
  }

  public final Map<String, List<String>> getParameters() {
    return parameters;
  }

  public final UIViewShowCommand addParameter(String name, List<String> values) {
    parameters.put(name, values);
    return this;
  }

}
