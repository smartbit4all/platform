package org.smartbit4all.ui.api.filter;

import org.smartbit4all.api.filter.bean.FilterGroup;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

public interface DynamicFilterViewModel extends ObjectEditing {

  @PublishEvents("OBJECT")
  ObservableObject dynamicFilterModel();

  @NotifyListeners
  void initModel(String uri);

  void setSelectorGroupVisible(String labelCode, boolean visible);

  FilterGroup getRootFilterGroup();
  
  void clearFilters();

}
