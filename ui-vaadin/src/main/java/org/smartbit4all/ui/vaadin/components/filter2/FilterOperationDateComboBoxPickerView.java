package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.TimeFilterOption;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.shared.Registration;

public class FilterOperationDateComboBoxPickerView extends FilterOperationDateIntervalView {

  private ComboBox<TimeFilterOption> comboTimeFilter;
  private VaadinHasValueBinder<TimeFilterOption, String> comboBinder;
  private Registration comboReg;
  private Registration beginDateReg;
  private Registration endDateReg;

  public FilterOperationDateComboBoxPickerView(ObservableObject filterField, String path) {
    super(filterField, path);
    comboTimeFilter = FilterViewUtils.createTimeFilterOptionCombo(this);
    addComponentAsFirst(comboTimeFilter);
    
    beginDateReg = FilterViewUtils.handleDateFilterValueChange(beginDate, comboTimeFilter);
    endDateReg = FilterViewUtils.handleDateFilterValueChange(endDate, comboTimeFilter);

    comboReg = FilterViewUtils.handleTimeFilterOptionComboChange(comboTimeFilter, filterField,
        path, false);
    comboBinder =
        FilterViewUtils.bindTimeFilterOptionCombo(comboTimeFilter, filterField, path, 3);
  }

  @Override
  public void unbind() {
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
