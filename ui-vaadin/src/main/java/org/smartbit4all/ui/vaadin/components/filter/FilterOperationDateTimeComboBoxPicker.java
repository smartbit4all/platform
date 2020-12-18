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
import org.smartbit4all.ui.common.filter.TimeFilterOptions;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;


public class FilterOperationDateTimeComboBoxPicker extends FilterOperationUI {

  ComboBox<TimeFilterOptions> cbTimeFilterOption;
  DateTimePicker beginDateTime;
  DateTimePicker endDateTime;


  public FilterOperationDateTimeComboBoxPicker() {
    addClassName("dynamic-filter-date");
    cbTimeFilterOption = new ComboBox<>();
    cbTimeFilterOption.setItems(TimeFilterOptions.values());
    cbTimeFilterOption.setItemLabelGenerator(option -> getTranslation(option.getLabel()));
    cbTimeFilterOption.setRequired(true);
    // TODO cbTimeFilterOption.addValueChangeListener(
    // e -> executeControllerCall(e, StatisticsViewController::changeTimeFilterOption));

    beginDateTime = new DateTimePicker();
    LocalDateTime now = LocalDateTime.now();
    beginDateTime.setMax(now);
    beginDateTime.addValueChangeListener(valueChangeListener());

    endDateTime = new DateTimePicker();
    endDateTime.setMax(now);
    endDateTime.addValueChangeListener(valueChangeListener());

    add(cbTimeFilterOption, beginDateTime, endDateTime);
  }

  private ValueChangeListener<? super ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> valueChangeListener() {
    return e -> {
      if (e.isFromClient()) {
        String beginDateString =
            beginDateTime.getValue() == null ? null : beginDateTime.getValue().toString();
        String endDateString =
            endDateTime.getValue() == null ? null : endDateTime.getValue().toString();
        String[] values = {beginDateString, endDateString};
        valueChanged(getFilterId(), values);
      }
    };
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO
  }

  @Override
  public void setValues(String... values) {
    if (values == null || values[0] == null) {
      beginDateTime.setValue(null);
      return;
    } else if (values[1] == null) {
      endDateTime.setValue(null);
      return;
    }

    if (values.length != 2) {
      throw new RuntimeException(
          "This method accepts 2 DateTimes, but " + values.length + " were given!");
    }

    beginDateTime.setValue(DateConverter.getDateTime(values[0]));
    endDateTime.setValue(DateConverter.getDateTime(values[1]));

    // TODO combobox

  }


  @Override
  public void setSelection(List<URI> list) {

  }

}
