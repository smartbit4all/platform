package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.TimeFilterOption;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.shared.Registration;

public class FilterOperationDateTimeComboBoxPickerView extends FilterOperationDateTimeIntervalView {

  private ComboBox<TimeFilterOption> comboTimeFilter;
  private VaadinHasValueBinder<TimeFilterOption, String> comboBinder;
  private Registration comboReg;
  private Registration beginDateReg;
  private Registration endDateReg;

  public FilterOperationDateTimeComboBoxPickerView(ObservableObject filterField, String path) {
    super(filterField, path);
    addClassName("dynamic-filter-date");
    comboTimeFilter = FilterViewUtils.createTimeFilterOptionCombo(this);
    addComponentAsFirst(comboTimeFilter);

    beginDateReg = FilterViewUtils.handleTimeFilterValueChange(beginDate, comboTimeFilter);
    endDateReg = FilterViewUtils.handleTimeFilterValueChange(endDate, comboTimeFilter);
    comboReg = FilterViewUtils.handleTimeFilterOptionComboChange(comboTimeFilter, filterField,
        path, true);
    comboBinder =
        FilterViewUtils.bindTimeFilterOptionCombo(comboTimeFilter, filterField, path, 3);
  }

  @Override
  public void unbind() {
    super.unbind();
    if (comboBinder != null) {
      comboBinder.unbind();
      comboBinder = null;
    }
    if (comboReg != null) {
      comboReg.remove();
      comboReg = null;
    }
    if (beginDateReg != null) {
      beginDateReg.remove();
      beginDateReg = null;
    }
    if (endDateReg != null) {
      endDateReg.remove();
      endDateReg = null;
    }
  }

  @Override
  public void setFilterEnabled(boolean enabled) {
    super.setFilterEnabled(enabled);
    comboTimeFilter.setEnabled(enabled);
  }

}
