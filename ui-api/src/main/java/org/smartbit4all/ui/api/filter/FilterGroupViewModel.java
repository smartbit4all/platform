package org.smartbit4all.ui.api.filter;

import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.ui.api.filter.model.FilterGroupModel;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;

public interface FilterGroupViewModel extends ObjectEditing {

  @PublishEvents("OBJECT")
  ObservableObject filterGroup();

  @NotifyListeners
  void setModel(FilterGroupModel filterGroupModel);

}
