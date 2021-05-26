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

import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.combobox.ComboBox;

public class FilterOperationComboBoxView extends FilterOperationView {

  private ComboBox<Value> comboBox;

  public FilterOperationComboBoxView(ObservableObject filterField, String path) {
    comboBox = new ComboBox<>();
    comboBox.addClassName("filter-combobox");
    comboBox.setItemLabelGenerator(Value::getDisplayValue);

    add(comboBox);

    VaadinBinders.bind(comboBox, filterField, path, "possibleValues");
    VaadinBinders.bind(comboBox, filterField, PathUtility.concatPath(path, "selectedValue"));

  }


  @Override
  public void setPlaceholder(String placeHolderText) {
    comboBox.setPlaceholder(placeHolderText);
  }


}
