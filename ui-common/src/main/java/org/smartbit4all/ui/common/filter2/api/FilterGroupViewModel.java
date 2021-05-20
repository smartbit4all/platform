package org.smartbit4all.ui.common.filter2.api;

import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.ui.common.filter2.model.FilterGroupModel;

public interface FilterGroupViewModel extends ObjectEditing {

  @PublishEvents("OBJECT")
  ObservableObject filterGroup();

  @NotifyListeners
  void setModel(FilterGroupModel filterGroupModel);

}
