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
import org.smartbit4all.ui.common.filter.DateConverter;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

public class FilterOperationDateTimeEquals extends FilterOperationUI {

  DateTimePicker dateTime;

  public FilterOperationDateTimeEquals() {
    dateTime = new DateTimePicker();
    LocalDateTime now = LocalDateTime.now();
    dateTime.setMax(now);
    dateTime.setValue(now);

    dateTime.addValueChangeListener(valueChangeListener());

    add(dateTime);
  }

  private ValueChangeListener<? super ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> valueChangeListener() {
    return e -> {
      if (e.isFromClient()) {
        String[] values = {dateTime.getValue().toString()};
        valueChanged(getFilterId(), values);
      }
    };
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValues(String... values) {
    if (values == null || values.length == 0 || values[0] == null) {
      dateTime.setValue(LocalDateTime.now());
      return;
    }
    // if (values.length != 1) {
    // throw new RuntimeException(
    // "This method accepts 1 DateTime, but " + values.length + " were given!");
    // }

    dateTime.setValue(DateConverter.getDateTime(values[0]));
  }

  @Override
  public void setSelection(List<URI> list) {
    // TODO Auto-generated method stub

  }

}
