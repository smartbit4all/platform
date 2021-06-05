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

  public FilterOperationDateTimeComboBoxPickerView(ObservableObject filterField, String path) {
    super(filterField, path);
    addClassName("dynamic-filter-date");
    comboTimeFilter = FilterViewUtils.createTimeFilterOptionCombo(this);
    addComponentAsFirst(comboTimeFilter);

    comboBinder =
        FilterViewUtils.bindTimeFilterOptionCombo(comboTimeFilter, filterField, path, 3);
    comboReg = FilterViewUtils.handleTimeFilterOptionComboChange(comboTimeFilter, filterField,
        path, true);
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
  }

  @Override
  public void setFilterEnabled(boolean enabled) {
    super.setFilterEnabled(enabled);
    comboTimeFilter.setEnabled(enabled);
  }

}
