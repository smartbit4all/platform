package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.TimeFilterOption;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.vaadin.components.binder.VaadinHasValueBinder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.shared.Registration;

public class FilterOperationDateComboBoxPickerView extends FilterOperationDateIntervalView {

  private ComboBox<TimeFilterOption> cbTimeFilterOption;
  private VaadinHasValueBinder<TimeFilterOption, String> comboBinder;
  private Registration comboReg;

  public FilterOperationDateComboBoxPickerView(ObservableObject filterField, String path) {
    super(filterField, path);
    cbTimeFilterOption = FilterViewUtils.createTimeFilterOptionCombo(this);
    addComponentAsFirst(cbTimeFilterOption);

  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    comboBinder =
        FilterViewUtils.bindTimeFilterOptionCombo(cbTimeFilterOption, filterField, path, 3);
    comboReg = FilterViewUtils.handleTimeFilterOptionComboChange(cbTimeFilterOption, filterField,
        path, false);
    super.onAttach(attachEvent);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    if (comboBinder != null) {
      comboBinder.unbind();
      comboBinder = null;
    }
    if (comboReg != null) {
      comboReg.remove();
      comboReg = null;
    }
    super.onDetach(detachEvent);
  }
}
