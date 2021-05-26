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
package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.core.object.ObservableObject;
import com.vaadin.flow.component.datepicker.DatePicker;

public class FilterOperationDateIntervalView extends FilterOperationView {

  private DatePicker beginDate;
  private DatePicker endDate;

  public FilterOperationDateIntervalView(ObservableObject filterField, String path) {

    beginDate = FilterViewUtils.createDatePicker();
    endDate = FilterViewUtils.createDatePicker();

    add(beginDate, endDate);

    FilterViewUtils.bindDate(beginDate, filterField, path, "value1");
    FilterViewUtils.bindDate(endDate, filterField, path, "value2");
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    beginDate.setPlaceholder(placeHolderText);
  }

}
