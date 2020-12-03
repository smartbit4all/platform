package org.smartbit4all.ui.vaadin.components.filter;

import java.time.LocalDateTime;
import java.util.List;
import org.smartbit4all.ui.vaadin.components.FlexBoxLayout;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

public class FilterOperationDateTimeInterval extends FilterOperationUI {

  String filterName;
  DateTimePicker beginDateTime;
  DateTimePicker endDateTime;

  public FilterOperationDateTimeInterval(String filterName) {
    this.filterName = filterName;
    addClassName("dynamic-filter-date-time");

    beginDateTime = new DateTimePicker();
    LocalDateTime now = LocalDateTime.now();
    beginDateTime.setMax(now);
    beginDateTime.setValue(now);

    endDateTime = new DateTimePicker();
    endDateTime.setMax(now);
    endDateTime.setValue(now);

    FlexBoxLayout startBox = new FlexBoxLayout(beginDateTime);
    startBox.setAlignItems(Alignment.END);
    startBox.setJustifyContentMode(JustifyContentMode.CENTER);
    add(startBox);
    FlexBoxLayout endBox = new FlexBoxLayout(endDateTime);
    endBox.setAlignItems(Alignment.END);
    endBox.setJustifyContentMode(JustifyContentMode.CENTER);
    add(endBox);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {


  }

  @Override
  public String getFilterName() {
    return filterName;
  }

  @Override
  public List<String> getPossibleOperations() {
    // TODO Auto-generated method stub
    return null;
  }

}
