package org.smartbit4all.ui.api.filter;

import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.ui.api.filter.model.FilterFieldModel;

public interface FilterFieldViewModel extends ObjectEditing {

  @PublishEvents("OBJECT")
  ObservableObject filterField();

  @NotifyListeners
  void setModel(FilterFieldModel filterFieldModel);

}
