package org.smartbit4all.ui.vaadin.components.filter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import org.smartbit4all.ui.common.filter.StatisticsTimeFilterOptions;
import org.smartbit4all.ui.vaadin.components.FlexBoxLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.timepicker.TimePicker;
// import hu.idomsoft.nova.border.stat.ui.api.chart.model.StatisticsTimeFilterOptions;
// import hu.idomsoft.nova.border.stat.ui.common.statview.StatisticsViewController;
// import hu.idomsoft.nova.border.stat.ui.common.statview.StatisticsViewController.FilterGroupIds;
// import hu.idomsoft.nova.border.stat.ui.vaadin.utils.StatUIUtils;

public class DynamicFilterOperationDateIntervalPicker extends DynamicFilterOperationUI {

  String filterName;
  ComboBox<StatisticsTimeFilterOptions> cbTimeFilterOption;
  DatePicker startDate;
  DatePicker endDate;
  TimePicker startTime;
  TimePicker endTime;

  public DynamicFilterOperationDateIntervalPicker(String filterName) {
    this.filterName = filterName;
    addClassName("dynamic-filter-date");
    cbTimeFilterOption = new ComboBox<>();
    cbTimeFilterOption.setItems(StatisticsTimeFilterOptions.values());
    cbTimeFilterOption.setItemLabelGenerator(option -> getTranslation(option.getLabel()));
    cbTimeFilterOption.setRequired(true);
//    cbTimeFilterOption.addValueChangeListener(
//        e -> executeControllerCall(e, StatisticsViewController::changeTimeFilterOption));

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

}
