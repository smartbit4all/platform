package org.smartbit4all.ui.vaadin.components.filter;

import java.time.LocalDateTime;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

public class FilterOperationDateTimeEquals extends FilterOperationUI {

  DateTimePicker dateTime;

  public FilterOperationDateTimeEquals() {
    dateTime = new DateTimePicker();
    LocalDateTime now = LocalDateTime.now();
    dateTime.setMax(now);
    dateTime.setValue(now);

    add(dateTime);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO Auto-generated method stub

  }

}
