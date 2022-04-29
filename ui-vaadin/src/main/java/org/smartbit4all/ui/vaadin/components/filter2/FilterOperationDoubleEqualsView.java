/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import com.vaadin.flow.component.textfield.NumberField;

public class FilterOperationDoubleEqualsView extends FilterOperationView {

  private NumberField numberField;
  private VaadinHasValueBinder<Double, String> binder;

  public FilterOperationDoubleEqualsView(ObservableObject filterField, String path) {
    super(filterField, path);
    addClassName("filter-onefield");
    addClassName("filter-integer-field");
    numberField = new NumberField();
    add(numberField);

    binder = FilterViewUtils.bindNumber(numberField, filterField, path, 1);
  }

  @Override
  public void unbind() {
    if (binder != null) {
      binder.unbind();
      binder = null;
    }
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    numberField.setPlaceholder(placeHolderText);
  }

  @Override
  public void setFilterEnabled(boolean enabled) {
    numberField.setEnabled(enabled);
  }

}
