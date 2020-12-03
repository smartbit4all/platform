package org.smartbit4all.ui.vaadin.components.filter;

import java.time.LocalDateTime;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

public class FilterOperationDateTimeInterval extends FilterOperationUI {

  DateTimePicker beginDateTime;
  DateTimePicker endDateTime;

  public FilterOperationDateTimeInterval() {
    addClassName("filter-date-time");

    beginDateTime = new DateTimePicker();
    LocalDateTime now = LocalDateTime.now();
    beginDateTime.setMax(now);
    beginDateTime.setValue(now);

    endDateTime = new DateTimePicker();
    endDateTime.setMax(now);
    endDateTime.setValue(now);

    add(beginDateTime, endDateTime);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO
  }
}
