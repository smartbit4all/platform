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

import java.util.List;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasItemsBinder;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUpList;
import org.springframework.util.StringUtils;

public class FilterOperationMultiSelectView extends FilterOperationView {

  protected MultiSelectPopUpList<Value> popUp;
  protected VaadinHasItemsBinder<Value> popupBinder;
  protected VaadinHasValueBinder<List<Value>, List<Value>> selectionBinder;

  public FilterOperationMultiSelectView(ObservableObject filterField, String path) {
    super(filterField, path);
    addClassName("filter-multi");
    popUp = new MultiSelectPopUpList<>();
    popUp.setRequired(false);
    popUp.setFilter((value, filterValue) -> {
      String displayedValue = value.getDisplayValue();
      String filter = String.valueOf(filterValue);
      return StringUtils.startsWithIgnoreCase(displayedValue, filter);
    });
    popUp.setItemDisplayValueProvider(Value::getDisplayValue);

    add(popUp);

    popupBinder = VaadinBinders.bindItems(popUp, filterField, path, "possibleValues");
    selectionBinder = VaadinBinders.bindValue(popUp, filterField, path + "/selectedValues");
  }

  @Override
  public void unbind() {
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

  @Override
  public void setFilterEnabled(boolean enabled) {
    popUp.setEnabled(enabled);
  }

}
