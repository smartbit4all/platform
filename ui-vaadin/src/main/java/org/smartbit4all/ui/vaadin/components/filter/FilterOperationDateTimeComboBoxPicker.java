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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.ui.common.filter.DateConverter;
import org.smartbit4all.ui.common.filter.FilterValueChangeListener;
import org.smartbit4all.ui.common.filter.TimeFilterOptions;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;


public class FilterOperationDateTimeComboBoxPicker extends FilterOperationUI {

  private FilterValueChangeListener filterValueChangeListener;

  private ComboBox<TimeFilterOptions> cbTimeFilterOption;
  private DateTimePicker beginDateTime;
  private DateTimePicker endDateTime;


  public FilterOperationDateTimeComboBoxPicker(
      FilterValueChangeListener filterValueChangeListener) {
    this.filterValueChangeListener = filterValueChangeListener;

    addClassName("dynamic-filter-date");
    cbTimeFilterOption = new ComboBox<>();
    cbTimeFilterOption.setItems(TimeFilterOptions.values());
    cbTimeFilterOption.setItemLabelGenerator(option -> getTranslation(option.getLabel()));
    cbTimeFilterOption.setRequired(true);
    cbTimeFilterOption.addValueChangeListener(cbListener());

    beginDateTime = new DateTimePicker();
    LocalDateTime now = LocalDateTime.now();
    beginDateTime.setMax(now);
    beginDateTime.addValueChangeListener(dateTimeListener());

    endDateTime = new DateTimePicker();
    endDateTime.setMax(now);
    endDateTime.addValueChangeListener(dateTimeListener());

    add(cbTimeFilterOption, beginDateTime, endDateTime);
  }

  private ValueChangeListener<? super ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> dateTimeListener() {
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
    String fromString =
        beginDateTime.getValue() == null ? null : beginDateTime.getValue().toString();
    String toString = endDateTime.getValue() == null ? null : endDateTime.getValue().toString();
    String filterOption =
        cbTimeFilterOption.getValue() == null ? null : cbTimeFilterOption.getValue().toString();
    FilterOperandValue value1 =
        new FilterOperandValue().type(LocalDateTime.class.getName()).value(fromString);
    FilterOperandValue value2 =
        new FilterOperandValue().type(LocalDateTime.class.getName()).value(toString);
    FilterOperandValue value3 =
        new FilterOperandValue().type(String.class.getName()).value(filterOption);
    filterValueChangeListener.filterValueChanged(filterId, value1, value2, value3);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO
  }

  @Override
  public void setValues(FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3) {
    if (value1 == null || value1.getValue() == null) {
      beginDateTime.setValue(null);
    } else {
      beginDateTime.setValue(DateConverter.getDateTime(value1.getValue()));
    }
    if (value2 == null || value2.getValue() == null) {
      endDateTime.setValue(null);
    } else {
      endDateTime.setValue(DateConverter.getDateTime(value2.getValue()));
    }
    if (value3 == null || value3.getValue() == null) {
      if (cbTimeFilterOption.getValue() != null) {
        cbTimeFilterOption.setValue(null);
      }
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
    // NOP
  }

}
