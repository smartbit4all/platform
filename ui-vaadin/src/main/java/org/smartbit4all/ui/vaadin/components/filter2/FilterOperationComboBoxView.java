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
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasItemsBinder;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.combobox.ComboBox;

public class FilterOperationComboBoxView extends FilterOperationView {

  private ComboBox<Value> comboBox;
  private VaadinHasItemsBinder<Value> comboBinder;
  private VaadinHasValueBinder<Value, Value> selectionBinder;

  public FilterOperationComboBoxView(ObservableObject filterField, String path) {
    super(filterField, path);
    comboBox = new ComboBox<>();
    comboBox.addClassName("filter-combobox");
    comboBox.setItemLabelGenerator(Value::getDisplayValue);
    add(comboBox);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    comboBinder = VaadinBinders.bind(comboBox, filterField, path, "possibleValues");
    selectionBinder =
        VaadinBinders.bind(comboBox, filterField, PathUtility.concatPath(path, "selectedValue"));
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    if (comboBinder != null) {
      comboBinder.unbind();
      comboBinder = null;
    }
    if (selectionBinder != null) {
      selectionBinder.unbind();
      selectionBinder = null;
    }
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    comboBox.setPlaceholder(placeHolderText);
  }


}
