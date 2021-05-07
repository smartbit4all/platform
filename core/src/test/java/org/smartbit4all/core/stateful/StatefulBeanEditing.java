package org.smartbit4all.core.stateful;

import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;

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
