package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.TimeFilterOption;
import org.smartbit4all.core.object.ObservableObject;
import com.vaadin.flow.component.combobox.ComboBox;

public class FilterOperationDateTimeComboBoxPickerView extends FilterOperationDateTimeIntervalView {


  public FilterOperationDateTimeComboBoxPickerView(ObservableObject filterField, String path) {
    super(filterField, path);
    addClassName("dynamic-filter-date");
    ComboBox<TimeFilterOption> cbTimeFilterOption =
        FilterViewUtils.createTimeFilterOptionCombo(this);
    addComponentAsFirst(cbTimeFilterOption);

    FilterViewUtils.bindTimeFilterOptionCombo(cbTimeFilterOption, filterField, path, 3);
  }

}
