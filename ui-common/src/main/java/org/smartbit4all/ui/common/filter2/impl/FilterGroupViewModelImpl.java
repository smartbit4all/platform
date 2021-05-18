package org.smartbit4all.ui.common.filter2.impl;

import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.common.filter2.api.FilterGroupViewModel;
import org.smartbit4all.ui.common.filter2.model.FilterGroupModel;

public class FilterGroupViewModelImpl extends ObjectEditingImpl implements FilterGroupViewModel {

  private ObservableObjectImpl filterGroup;

  private FilterGroupModel filterGroupModel;

  private FilterGroupViewModel parentViewModel;

  public FilterGroupViewModelImpl(FilterGroupViewModel parentViewModel) {
    this.parentViewModel = parentViewModel;
    filterGroup = new ObservableObjectImpl();
  }

  @Override
  public void setModel(FilterGroupModel filterGroupModel) {
    this.filterGroupModel = filterGroupModel;
    ref = new ApiObjectRef(null, filterGroupModel, ViewModelHelper.getFilterDescriptors());
    filterGroup.setRef(ref);
    filterGroup.notifyListeners();
  }

  @Override
  public ObservableObject filterGroup() {
    return filterGroup;
  }

}
