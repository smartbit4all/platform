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

  public FilterOperationDateComboBoxPickerView(ObservableObject filterField, String path) {
    super(filterField, path);
    comboTimeFilter = FilterViewUtils.createTimeFilterOptionCombo(this);
    addComponentAsFirst(comboTimeFilter);

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
  }

  @Override
  public void setFilterEnabled(boolean enabled) {
    super.setFilterEnabled(enabled);
    comboTimeFilter.setEnabled(enabled);
  }

}
