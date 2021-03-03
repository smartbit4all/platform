package org.smartbit4all.api.stateful;

import org.smartbit4all.api.object.NotifyListeners;
import org.smartbit4all.api.object.ObjectEditing;

public interface StatefulBeanEditing extends ObjectEditing{
  void setBean(StatefulBean bean);

  void setValue(String name);

  @NotifyListeners
  void setValueAndNotifyAll(String name);

  @NotifyListeners("OBJECT")
  void setValueAndNotify(String name);

  @NotifyListeners("OTHER")
  void setValueAndNotifyOther(String name);
}
