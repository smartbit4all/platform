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
import org.smartbit4all.api.filter.DateConverter;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.ui.common.filter.FilterValueChangeListener;
import org.smartbit4all.ui.vaadin.localization.ComponentLocalizations;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.datepicker.DatePicker;

public class FilterOperationDateEquals extends FilterOperationUI {

  private FilterValueChangeListener filterValueChangeListener;

  private DatePicker date;

  public FilterOperationDateEquals(FilterValueChangeListener filterValueChangeListener) {
    this.filterValueChangeListener = filterValueChangeListener;

    date = new DatePicker();
    LocalDate now = LocalDate.now();
    date.setMax(now);
    date.addValueChangeListener(valueChangeListener());
    
    ComponentLocalizations.localize(date);

    add(date);
  }

  private ValueChangeListener<? super ComponentValueChangeEvent<DatePicker, LocalDate>> valueChangeListener() {
    return e -> {
      if (e.isFromClient()) {
        String dateString = date.getValue() == null ? null : date.getValue().toString();
        FilterOperandValue value1 =
            new FilterOperandValue().type(LocalDate.class.getName()).value(dateString);
        filterValueChangeListener.filterValueChanged(filterId, value1, null, null);
      }
    };
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValues(FilterOperandValue value1, FilterOperandValue value2,
      FilterOperandValue value3) {
    if (value1 == null || value1.getValue() == null) {
      date.setValue(null);
      return;
    }
    date.setValue(DateConverter.getDate(value1.getValue()));
  }

  @Override
  public void setSelection(List<URI> list) {
    // TODO Auto-generated method stub

  }


}
