package org.smartbit4all.ui.vaadin.components.filter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.smartbit4all.ui.common.filter.TimeFilterOptions;
import org.smartbit4all.ui.vaadin.components.FlexBoxLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.timepicker.TimePicker;


public class DynamicFilterOperationDateIntervalPicker extends DynamicFilterOperationUI {

  String filterName;
  ComboBox<TimeFilterOptions> cbTimeFilterOption;
  DatePicker startDate;
  DatePicker endDate;
  TimePicker startTime;
  TimePicker endTime;


  public DynamicFilterOperationDateIntervalPicker(String filterName) {
    this.filterName = filterName;
    addClassName("dynamic-filter-date");
    cbTimeFilterOption = new ComboBox<>();
    cbTimeFilterOption.setItems(TimeFilterOptions.values());
    cbTimeFilterOption.setItemLabelGenerator(option -> getTranslation(option.getLabel()));
    cbTimeFilterOption.setRequired(true);
    // cbTimeFilterOption.addValueChangeListener(
    // e -> executeControllerCall(e, StatisticsViewController::changeTimeFilterOption));

    LocalDate today = LocalDate.now();

    startDate = new DatePicker();
    startDate.setClearButtonVisible(false);
    startDate.setRequired(true);
    startDate.setMax(today);
    startDate.setReadOnly(false);
    startDate.setValue(LocalDate.now());

    startTime = new TimePicker();
    startTime.setStep(Duration.ofMinutes(30));
    startTime.setRequired(true);
    startTime.setReadOnly(false);
    startTime.setClearButtonVisible(false);
    startTime.setValue(LocalTime.now());

    endDate = new DatePicker();
    endDate.setClearButtonVisible(false);
    endDate.setRequired(true);
    endDate.setMax(today);
    endDate.setReadOnly(false);
    endDate.setValue(LocalDate.now());

    endTime = new TimePicker();
    endTime.setStep(Duration.ofMinutes(30));
    endTime.setRequired(true);
    endTime.setReadOnly(false);
    endTime.setClearButtonVisible(false);
    endTime.setValue(LocalTime.now());

    startDate.setMaxWidth("140px");
    startTime.setMaxWidth("90px");
    endDate.setMaxWidth("140px");
    endTime.setMaxWidth("90px");

    FlexBoxLayout timeBox = new FlexBoxLayout(cbTimeFilterOption);
    timeBox.setAlignItems(Alignment.CENTER);
    add(timeBox);
    FlexBoxLayout startBox = new FlexBoxLayout(startDate, startTime);
    startBox.setAlignItems(Alignment.END);
    startBox.setJustifyContentMode(JustifyContentMode.CENTER);
    add(startBox);
    FlexBoxLayout endBox = new FlexBoxLayout(endDate, endTime);
    endBox.setAlignItems(Alignment.END);
    endBox.setJustifyContentMode(JustifyContentMode.CENTER);
    add(endBox);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    startDate.setPlaceholder(placeHolderText);

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
