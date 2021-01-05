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
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.ui.common.filter.DateConverter;
import org.smartbit4all.ui.common.filter.TimeFilterOptions;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;

public class FilterOperationDateComboBoxPicker extends FilterOperationUI {

  DatePicker beginDate;
  DatePicker endDate;
  ComboBox<TimeFilterOptions> cbTimeFilterOption;

  public FilterOperationDateComboBoxPicker() {
    cbTimeFilterOption = new ComboBox<>();
    cbTimeFilterOption.setItems(TimeFilterOptions.values());
    cbTimeFilterOption.setItemLabelGenerator(option -> getTranslation(option.getLabel()));
    cbTimeFilterOption.setRequired(true);
    cbTimeFilterOption.addValueChangeListener(cbListener());

    beginDate = new DatePicker();
    LocalDate now = LocalDate.now();
    beginDate.setMax(now);
    beginDate.addValueChangeListener(dateListener());

    endDate = new DatePicker();
    endDate.setMax(now);
    endDate.addValueChangeListener(dateListener());

    add(cbTimeFilterOption, beginDate, endDate);
  }


  private ValueChangeListener<? super ComponentValueChangeEvent<DatePicker, LocalDate>> dateListener() {
    return e -> {
      if (e.isFromClient()) {
        propagateValueChange();
      }
    };
  }

  private ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<TimeFilterOptions>, TimeFilterOptions>> cbListener() {
    return e -> {
      if (e.isFromClient()) {
        propagateValueChange();
      }
    };
  }

  private void propagateValueChange() {
    String fromString = beginDate.getValue() == null ? null : beginDate.getValue().toString();
    String toString = endDate.getValue() == null ? null : endDate.getValue().toString();
    String filterOption = cbTimeFilterOption.getValue().toString();
    FilterOperandValue value1 =
        new FilterOperandValue().type(LocalDate.class.getName()).value(fromString);
    FilterOperandValue value2 =
        new FilterOperandValue().type(LocalDate.class.getName()).value(toString);
    FilterOperandValue value3 =
        new FilterOperandValue().type(String.class.getName()).value(filterOption);
    valueChanged(getFilterId(), value1, value2, value3);
  }


  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO Auto-generated method stub
  }


  @Override
  public void setValues(FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3) {
    if (value1 == null || value1.getValue() == null) {
      beginDate.setValue(null);
    } else {
      beginDate.setValue(DateConverter.getDate(value1.getValue()));
    }
    if (value2 == null || value2.getValue() == null) {
      endDate.setValue(null);
    } else {
      endDate.setValue(DateConverter.getDate(value2.getValue()));
    }
    if (value3 == null || value3.getValue() == null) {
      // cbTimeFilterOption.setValue(null);
    } else {
      TimeFilterOptions newValue = TimeFilterOptions.valueOf(value3.getValue());
      TimeFilterOptions oldValue = cbTimeFilterOption.getValue();
      if (!Objects.equals(newValue, oldValue)) {
        cbTimeFilterOption.setValue(newValue);
      }
    }
  }


  @Override
  public void setSelection(List<URI> list) {
    // NONE
  }

}
