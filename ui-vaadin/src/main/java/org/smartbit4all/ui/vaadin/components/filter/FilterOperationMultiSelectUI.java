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
package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUp;
import org.smartbit4all.ui.vaadin.util.UIUtils;

public class FilterOperationMultiSelectUI extends FilterOperationUI {

  private MultiSelectPopUp<Value> popUp;

  public FilterOperationMultiSelectUI(List<Value> possibleValues) {
    addClassName("filter-multi");
    popUp = new MultiSelectPopUp<>();
    popUp.setItems(possibleValues);
    popUp.setRequired(false);
    // popUp.setFilter(filter); TODO
    popUp.setItemDisplayValueProvider(v -> v.getDisplayValue());

    popUp.addValueChangeListener(e -> {
      UIUtils.showNotification("value changed");
    });

    add(popUp);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    popUp.setPlaceholder(placeHolderText);
  }

}
