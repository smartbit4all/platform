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

import java.time.LocalDate;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.datepicker.DatePicker;

public class FilterOperationDateIntervalView extends FilterOperationView {

  private DatePicker beginDate;
  private DatePicker endDate;
  private VaadinHasValueBinder<LocalDate, String> beginDateBinder;
  private VaadinHasValueBinder<LocalDate, String> endDateBinder;

  public FilterOperationDateIntervalView(ObservableObject filterField, String path) {
    super(filterField, path);
    beginDate = FilterViewUtils.createDatePicker();
    endDate = FilterViewUtils.createDatePicker();

    add(beginDate, endDate);

  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    beginDate.setPlaceholder(placeHolderText);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    beginDateBinder = FilterViewUtils.bindDate(beginDate, filterField, path, 1);
    endDateBinder = FilterViewUtils.bindDate(endDate, filterField, path, 2);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    if (beginDateBinder != null) {
      beginDateBinder.unbind();
      beginDateBinder = null;
    }
    if (endDateBinder != null) {
      endDateBinder.unbind();
      endDateBinder = null;
    }
  }
}
