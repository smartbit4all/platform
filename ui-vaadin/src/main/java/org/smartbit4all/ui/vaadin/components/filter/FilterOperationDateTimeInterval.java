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
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.ui.common.filter.DateConverter;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

public class FilterOperationDateTimeInterval extends FilterOperationUI {

  DateTimePicker beginDateTime;
  DateTimePicker endDateTime;

  public FilterOperationDateTimeInterval() {
    addClassName("filter-date-time");
    LocalDateTime now = LocalDateTime.now();

    beginDateTime = new DateTimePicker();
    beginDateTime.setMax(now);
    beginDateTime.addValueChangeListener(valueChangeListener());

    endDateTime = new DateTimePicker();
    endDateTime.setMax(now);
    endDateTime.addValueChangeListener(valueChangeListener());

    add(beginDateTime, endDateTime);
  }

  private ValueChangeListener<? super ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> valueChangeListener() {
    return e -> {
      if (e.isFromClient()) {
        String fromString =
            beginDateTime.getValue() == null ? null : beginDateTime.getValue().toString();
        String toString = endDateTime.getValue() == null ? null : endDateTime.getValue().toString();
        FilterOperandValue value1 =
            new FilterOperandValue().type(LocalDateTime.class.getName()).value(fromString);
        FilterOperandValue value2 =
            new FilterOperandValue().type(LocalDateTime.class.getName()).value(toString);
        valueChanged(getFilterId(), value1, value2, null);
      }
    };
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
  }

  @Override
  public void setSelection(List<URI> list) {
    // TODO Auto-generated method stub

  }
}
