package org.smartbit4all.ui.api.data.storage.history;

import java.util.List;
import org.smartbit4all.api.storage.bean.ObjectHistoryEntry;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;

public interface ObjectHistoryViewModel extends ObjectEditing {
  
  public static final String SET_OBJECT = "setObject";
  public static final String OPEN_VERSION = "openVersion";
  
  @PublishEvents("OBJECT_HISTORY")
  ObservableObject objectHistory();
  
  @NotifyListeners
  void setHistoryEntries(List<ObjectHistoryEntry> entries);
}
