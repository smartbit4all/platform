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

import java.time.LocalDateTime;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

public class FilterOperationDateTimeIntervalView extends FilterOperationView {

  private DateTimePicker beginDate;
  private DateTimePicker endDate;
  private VaadinHasValueBinder<LocalDateTime, String> beginDateBinder;
  private VaadinHasValueBinder<LocalDateTime, String> endDateBinder;

  public FilterOperationDateTimeIntervalView(ObservableObject filterField, String path) {
    super(filterField, path);
    addClassName("filter-date-time");

    beginDate = FilterViewUtils.createDateTimePicker();
    endDate = FilterViewUtils.createDateTimePicker();

    add(beginDate, endDate);

    beginDateBinder = FilterViewUtils.bindDateTime(beginDate, filterField, path, 1);
    endDateBinder = FilterViewUtils.bindDateTime(endDate, filterField, path, 2);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    beginDate.setDatePlaceholder(placeHolderText);
  }


  @Override
  public void unbind() {
    if (beginDateBinder != null) {
      beginDateBinder.unbind();
      beginDateBinder = null;
    }
    if (endDateBinder != null) {
      endDateBinder.unbind();
      endDateBinder = null;
    }
  }

  @Override
  public void setFilterEnabled(boolean enabled) {
    beginDate.setEnabled(enabled);
    endDate.setEnabled(enabled);
  }

}
