package org.smartbit4all.core.stateful;

import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

public interface StatefulBeanEditing extends ObjectEditing {

  @PublishEvents("OBJECT")
  ObservableObject publisher();

  void setBean(StatefulBean bean);

  void setValue(String name);

  @NotifyListeners
  void setValueAndNotifyAll(String name);

  @NotifyListeners("OBJECT")
  void setValueAndNotify(String name);

  @NotifyListeners("OTHER")
  void setValueAndNotifyOther(String name);
}
