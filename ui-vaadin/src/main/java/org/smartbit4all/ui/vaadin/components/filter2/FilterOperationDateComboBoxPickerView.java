package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.TimeFilterOption;
import org.smartbit4all.core.object.ObservableObject;
import com.vaadin.flow.component.combobox.ComboBox;

public class FilterOperationDateComboBoxPickerView extends FilterOperationDateIntervalView {

  private ComboBox<TimeFilterOption> cbTimeFilterOption;

  public FilterOperationDateComboBoxPickerView(ObservableObject filterField, String path) {
    super(filterField, path);
    cbTimeFilterOption = FilterViewUtils.createTimeFilterOptionCombo(this);
    addComponentAsFirst(cbTimeFilterOption);

    FilterViewUtils.bindTimeFilterOptionCombo(cbTimeFilterOption, filterField, path, "value3");
  }

}
