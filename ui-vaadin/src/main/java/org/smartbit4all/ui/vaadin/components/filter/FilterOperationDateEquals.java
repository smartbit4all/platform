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

public class FilterOperationDateEquals extends FilterOperationUI {

  DatePicker date;

  public FilterOperationDateEquals() {
    date = new DatePicker();
    LocalDate now = LocalDate.now();
    date.setMax(now);
    date.setValue(now);

    add(date);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValues(String... values) {
    if (values == null || values[0] == null) {
      date.setValue(LocalDate.now());
      return;
    }
    if (values.length != 2) {
      throw new RuntimeException(
          "This method accepts 1 Date, but " + values.length + " were given!");
    }

    date.setValue(DateConverter.getDate(values[0]));

  }

  @Override
  public void setSelection(List<URI> list) {
    // TODO Auto-generated method stub

  }


}
