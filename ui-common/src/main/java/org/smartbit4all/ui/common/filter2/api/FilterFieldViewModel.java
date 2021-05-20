package org.smartbit4all.ui.common.filter2.api;

import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.ui.common.filter2.model.FilterFieldModel;

public interface FilterFieldViewModel extends ObjectEditing {

  @PublishEvents("OBJECT")
  ObservableObject filterField();

  @NotifyListeners
  void setModel(FilterFieldModel filterFieldModel);

}
