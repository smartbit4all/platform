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

public class FilterOperationDateInterval extends FilterOperationUI {

  private FilterValueChangeListener filterValueChangeListener;

  private DatePicker beginDate;
  private DatePicker endDate;

  public FilterOperationDateInterval(FilterValueChangeListener filterValueChangeListener) {
    this.filterValueChangeListener = filterValueChangeListener;

    beginDate = new DatePicker();
    LocalDate now = LocalDate.now();
    beginDate.setMax(now);
    beginDate.addValueChangeListener(valueChangeListener());

    endDate = new DatePicker();
    endDate.setMax(now);
    endDate.addValueChangeListener(valueChangeListener());
    
    ComponentLocalizations.localize(beginDate);
    ComponentLocalizations.localize(endDate);

    add(beginDate, endDate);
  }

  private ValueChangeListener<? super ComponentValueChangeEvent<DatePicker, LocalDate>> valueChangeListener() {
    return e -> {
      if (e.isFromClient()) {
        String fromValue = beginDate.getValue() == null ? null : beginDate.getValue().toString();
        String toValue = endDate.getValue() == null ? null : endDate.getValue().toString();
        FilterOperandValue value1 =
            new FilterOperandValue().type(LocalDate.class.getName()).value(fromValue);
        FilterOperandValue value2 =
            new FilterOperandValue().type(LocalDate.class.getName()).value(toValue);
        filterValueChangeListener.filterValueChanged(filterId, value1, value2, null);
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
      beginDate.setValue(null);
    } else {
      beginDate.setValue(DateConverter.getDate(value1.getValue()));
    }
    if (value2 == null || value2.getValue() == null) {
      endDate.setValue(null);
    } else {
      endDate.setValue(DateConverter.getDate(value2.getValue()));
    }
  }

  @Override
  public void setSelection(List<URI> list) {
    // TODO Auto-generated method stub

  }

}
