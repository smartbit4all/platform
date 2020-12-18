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
import java.util.Arrays;
import java.util.List;
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
  private List<TimeFilterOptions> possibleValues;

  public FilterOperationDateComboBoxPicker() {
    cbTimeFilterOption = new ComboBox<>();
    cbTimeFilterOption.setItems(TimeFilterOptions.values());
    cbTimeFilterOption.setItemLabelGenerator(option -> getTranslation(option.getLabel()));
    cbTimeFilterOption.setRequired(true);
    this.possibleValues = Arrays.asList(TimeFilterOptions.values());
    // cbTimeFilterOption.addValueChangeListener(
    // e -> executeControllerCall(e, StatisticsViewController::changeTimeFilterOption));


    beginDate = new DatePicker();
    LocalDate now = LocalDate.now();
    beginDate.setMax(now);
    beginDate.addValueChangeListener(valueChangeListener());

    endDate = new DatePicker();
    endDate.setMax(now);
    endDate.addValueChangeListener(valueChangeListener());

    add(cbTimeFilterOption, beginDate, endDate);
  }


  private ValueChangeListener<? super ComponentValueChangeEvent<DatePicker, LocalDate>> valueChangeListener() {
    return e -> {
      if (e.isFromClient()) {
        String beginDateString =
            beginDate.getValue() == null ? null : beginDate.getValue().toString();
        String endDateString = endDate.getValue() == null ? null : endDate.getValue().toString();
        String[] values = {beginDateString, endDateString};
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
    if (values == null || values[0] == null) {
      beginDate.setValue(null);
      return;
    } else if (values[1] == null) {
      endDate.setValue(null);
      return;
    }

    if (values.length != 2) {
      throw new RuntimeException(
          "This method accepts 2 Dates, but " + values.length + " were given!");
    }

    beginDate.setValue(DateConverter.getDate(values[0]));
    endDate.setValue(DateConverter.getDate(values[1]));



    // TODO combobox

  }


  @Override
  public void setSelection(List<URI> list) {

    if (list == null || list.isEmpty()) {
      cbTimeFilterOption.setValue(null);
      return;
    }
    if (list.size() > 1) {
      throw new RuntimeException("More than one value is not allowed!");
    }

    // URI uri = list.get(0);
    // Optional<Value> value =
    // possibleValues.stream().filter(v -> v.getObjectUri().equals(uri)).findFirst();
    // if (value.isPresent()) {
    // comboBox.setValue(value.get());
    // } else {
    // throw new RuntimeException("URI is not in given possibleValues list: " + uri);
    // }
  }

}
