package org.smartbit4all.ui.vaadin.components.filter;

import java.time.LocalDate;
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

}
