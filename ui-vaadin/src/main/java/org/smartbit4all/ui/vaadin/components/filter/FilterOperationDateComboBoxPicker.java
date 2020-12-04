package org.smartbit4all.ui.vaadin.components.filter;

import java.time.LocalDate;
import org.smartbit4all.ui.common.filter.TimeFilterOptions;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;

public class FilterOperationDateComboBoxPicker extends FilterOperationUI {

  DatePicker beginDate;
  DatePicker endDate;
  ComboBox<TimeFilterOptions> cbTimeFilterOption;

  public FilterOperationDateComboBoxPicker() {
    cbTimeFilterOption = new ComboBox<>();
    cbTimeFilterOption.setItems(TimeFilterOptions.values());
    cbTimeFilterOption.setItemLabelGenerator(option -> getTranslation(option.getLabel()));
    cbTimeFilterOption.setRequired(true);
    // cbTimeFilterOption.addValueChangeListener(
    // e -> executeControllerCall(e, StatisticsViewController::changeTimeFilterOption));

    beginDate = new DatePicker();
    LocalDate now = LocalDate.now();
    beginDate.setMax(now);
    beginDate.setValue(now);

    endDate = new DatePicker();
    endDate.setMax(now);
    endDate.setValue(now);

    add(cbTimeFilterOption, beginDate, endDate);
  }


  @Override
  public void setPlaceholder(String placeHolderText) {
    // TODO Auto-generated method stub
  }

}
