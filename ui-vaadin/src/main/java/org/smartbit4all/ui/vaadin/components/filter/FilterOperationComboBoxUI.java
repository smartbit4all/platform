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
import java.util.List;
import java.util.Optional;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.api.value.bean.Value;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.combobox.ComboBox;

public class FilterOperationComboBoxUI extends FilterOperationUI {
  ComboBox<Value> comboBox;
  private List<Value> possibleValues;

  public FilterOperationComboBoxUI(List<Value> possibleValues) {
    comboBox = new ComboBox<>();
    this.possibleValues = possibleValues;
    comboBox.addClassName("filter-combobox");
    comboBox.setItems(possibleValues);
    comboBox.setItemLabelGenerator(v -> v.getDisplayValue());
    comboBox.addValueChangeListener(valueChangeListener());


    add(comboBox);
  }

  private ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<Value>, Value>> valueChangeListener() {
    return e -> {
      if (e.isFromClient()) {
        List<URI> uriList = new ArrayList<>();
        uriList.add(comboBox.getValue().getObjectUri());
        selectionChanged(getFilterId(), uriList);
      }
    };
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    comboBox.setPlaceholder(placeHolderText);
  }

  @Override
  public void setValues(FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setSelection(List<URI> list) {
    if (list == null || list.isEmpty()) {
      comboBox.setValue(null);
      return;
    }
    if (list.size() > 1) {
      throw new RuntimeException("More than one value is not allowed!");
    }
    URI uri = list.get(0);
    Optional<Value> value =
        possibleValues.stream().filter(v -> v.getObjectUri().equals(uri)).findFirst();
    if (value.isPresent()) {
      comboBox.setValue(value.get());
    } else {
      throw new RuntimeException("URI is not in given possibleValues list: " + uri);
    }

  }



}
