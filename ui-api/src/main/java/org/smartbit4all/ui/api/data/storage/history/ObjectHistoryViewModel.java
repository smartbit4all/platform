package org.smartbit4all.ui.api.data.storage.history;

import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

public interface ObjectHistoryViewModel extends ObjectEditing {
  
  public static final String SET_OBJECT = "setObject";
  public static final String OPEN_VERSION = "openVersion";
  
  @PublishEvents("OBJECT_HISTORY")
  ObservableObject objectHistory();
}
