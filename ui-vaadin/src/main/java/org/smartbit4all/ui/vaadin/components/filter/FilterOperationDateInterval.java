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
import org.smartbit4all.ui.common.filter.DateConverter;
import com.vaadin.flow.component.datepicker.DatePicker;

public class FilterOperationDateInterval extends FilterOperationUI {

  DatePicker beginDate;
  DatePicker endDate;

  public FilterOperationDateInterval() {
    beginDate = new DatePicker();
    LocalDate now = LocalDate.now();
    beginDate.setMax(now);
    beginDate.setValue(now);

    endDate = new DatePicker();
    endDate.setMax(now);
    endDate.setValue(now);

    add(beginDate, endDate);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValues(String... values) {
    if (values == null || values[0] == null) {
      beginDate.setValue(LocalDate.now());
      endDate.setValue(LocalDate.now());
      return;
    }

    if (values.length != 2) {
      throw new RuntimeException(
          "This method accepts 2 Dates, but " + values.length + " were given!");
    }

    beginDate.setValue(DateConverter.getDate(values[0]));
    endDate.setValue(DateConverter.getDate(values[1]));

  }

  @Override
  public void setSelection(List<URI> list) {
    // TODO Auto-generated method stub

  }

}
