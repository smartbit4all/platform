package org.smartbit4all.ui.vaadin.components.filter;

import java.time.LocalDate;
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

}
