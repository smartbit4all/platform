/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.common.view;

import java.util.HashMap;
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
  private Map<String, Object> parameters;

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

  public final Map<String, Object> getParameters() {
    return parameters;
  }

  public final UIViewShowCommand addParameter(String name, Object value) {
    parameters.put(name, value);
    return this;
  }

}
