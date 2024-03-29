package org.smartbit4all.ui.common.filter2.impl;

import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.api.filter.FilterFieldViewModel;
import org.smartbit4all.ui.api.filter.model.FilterFieldModel;
import org.smartbit4all.ui.api.viewmodel.ObjectEditingImpl;

public class FilterFieldViewModelImpl extends ObjectEditingImpl implements FilterFieldViewModel {

  private ObservableObjectImpl filterFieldObservable;

  public FilterFieldViewModelImpl() {
    filterFieldObservable = new ObservableObjectImpl();
  }

  @Override
  public void setModel(FilterFieldModel filterFieldModel) {
    ref = new ApiObjectRef(null, filterFieldModel, ViewModelHelper.getFilterDescriptors());
    filterFieldObservable.setRef(ref);
  }

  @Override
  public ObservableObject filterField() {
    return filterFieldObservable;
  }

}
