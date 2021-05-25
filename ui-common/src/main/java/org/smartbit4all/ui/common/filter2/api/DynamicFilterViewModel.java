package org.smartbit4all.ui.common.filter2.api;

import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

public interface DynamicFilterViewModel extends ObjectEditing {

  @PublishEvents("OBJECT")
  ObservableObject dynamicFilterModel();

  @NotifyListeners
  void initModel(String uri);

}
