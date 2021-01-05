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
package org.smartbit4all.ui.vaadin.components.filter;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.ui.vaadin.components.selector.MultiSelectPopUp;

public class FilterOperationMultiSelectUI extends FilterOperationUI {

  private MultiSelectPopUp<Value> popUp;
  private List<Value> possibleValues;

  public FilterOperationMultiSelectUI(List<Value> possibleValues) {
    addClassName("filter-multi");
    popUp = new MultiSelectPopUp<>();
    popUp.setItems(possibleValues);
    this.possibleValues = possibleValues;
    popUp.setRequired(false);
    // popUp.setFilter(filter); TODO
    popUp.setItemDisplayValueProvider(v -> v.getDisplayValue());

    popUp.addValueChangeListener(e -> {
      if (e.isFromClient()) {
        List<URI> uriList = new ArrayList<>();
        for (Value value : popUp.getValue()) {
          uriList.add(value.getObjectUri());
        }
        selectionChanged(getFilterId(), uriList);
      }
    });

    add(popUp);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    popUp.setPlaceholder(placeHolderText);
  }

  @Override
  public void setValues(FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3) {
    // TODO
  }

  @Override
  public void setSelection(List<URI> list) {
    if (list == null || list.isEmpty()) {
      popUp.setValue(null);
      return;
    }

    Set<Value> valueSet = new HashSet<>();
    for (URI uri : list) {
      for (Value value : possibleValues) {
        if (value.getObjectUri().equals(uri)) {
          valueSet.add(value);
        }
      }
    }

    if (!valueSet.isEmpty()) {
      popUp.setValue(valueSet);
    } else {
      throw new RuntimeException("URIs are not in given possibleValues list: " + list);
    }
  }


}
