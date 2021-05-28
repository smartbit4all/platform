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
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasItemsBinder;
import org.smartbit4all.ui.vaadin.components.binder.VaadinMultiSelectBinder;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUp;
import org.springframework.util.StringUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.Grid;

public class FilterOperationMultiSelectView extends FilterOperationView {

  private MultiSelectPopUp<Value> popUp;
  private VaadinHasItemsBinder<Value> popupBinder;
  private VaadinMultiSelectBinder<Grid<Value>, Value> selectionBinder;

  public FilterOperationMultiSelectView(ObservableObject filterField, String path) {
    super(filterField, path);
    addClassName("filter-multi");
    popUp = new MultiSelectPopUp<>();
    popUp.setRequired(false);
    popUp.setFilter((value, filterValue) -> {
      String displayedValue = value.getDisplayValue();
      String filter = String.valueOf(filterValue);
      return StringUtils.startsWithIgnoreCase(displayedValue, filter);
    });
    popUp.setItemDisplayValueProvider(Value::getDisplayValue);

    add(popUp);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    popupBinder = VaadinBinders.bind(popUp, filterField, path, "possibleValues");
    selectionBinder =
        VaadinBinders.bindSelection(popUp.asMultiselect(), filterField, path, "selectedValues");
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    if (popupBinder != null) {
      popupBinder.unbind();
      popupBinder = null;
    }
    if (selectionBinder != null) {
      selectionBinder.unbind();
      selectionBinder = null;
    }
  }


  @Override
  public void setPlaceholder(String placeHolderText) {
    popUp.setPlaceholder(placeHolderText);
  }

}
