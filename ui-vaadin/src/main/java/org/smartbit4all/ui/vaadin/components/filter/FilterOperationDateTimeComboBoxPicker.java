package org.smartbit4all.ui.vaadin.components.filter;

import java.time.LocalDateTime;
import org.smartbit4all.ui.common.filter.TimeFilterOptions;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;


public class FilterOperationDateTimeComboBoxPicker extends FilterOperationUI {

  ComboBox<TimeFilterOptions> cbTimeFilterOption;
  DateTimePicker beginDateTime;
  DateTimePicker endDateTime;


  public FilterOperationDateTimeComboBoxPicker() {
    addClassName("dynamic-filter-date");
    cbTimeFilterOption = new ComboBox<>();
    cbTimeFilterOption.setItems(TimeFilterOptions.values());
    cbTimeFilterOption.setItemLabelGenerator(option -> getTranslation(option.getLabel()));
    cbTimeFilterOption.setRequired(true);
    // cbTimeFilterOption.addValueChangeListener(
    // e -> executeControllerCall(e, StatisticsViewController::changeTimeFilterOption));

    beginDateTime = new DateTimePicker();
    LocalDateTime now = LocalDateTime.now();
    beginDateTime.setMax(now);
    beginDateTime.setValue(now);

    endDateTime = new DateTimePicker();
    endDateTime.setMax(now);
    endDateTime.setValue(now);

    add(cbTimeFilterOption, beginDateTime, endDateTime);
  }

  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO
  }

}
