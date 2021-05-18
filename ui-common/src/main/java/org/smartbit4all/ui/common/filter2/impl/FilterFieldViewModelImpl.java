package org.smartbit4all.ui.common.filter2.impl;

import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.common.filter2.api.FilterFieldViewModel;
import org.smartbit4all.ui.common.filter2.api.FilterGroupViewModel;
import org.smartbit4all.ui.common.filter2.model.FilterFieldModel;

public class FilterFieldViewModelImpl extends ObjectEditingImpl implements FilterFieldViewModel {

  private ObservableObjectImpl filterField;

  private FilterFieldModel filterFieldModel;

  private FilterGroupViewModel parentViewModel;

  public FilterFieldViewModelImpl(FilterGroupViewModel parentViewModel) {
    this.parentViewModel = parentViewModel;
  }

  @Override
  public void setModel(FilterFieldModel filterFieldModel) {
    this.filterFieldModel = filterFieldModel;
    ref = new ApiObjectRef(null, filterFieldModel, ViewModelHelper.getFilterDescriptors());
    filterField.setRef(ref);
  }

  @Override
  public ObservableObject filterField() {
    return filterField;
  }

}
