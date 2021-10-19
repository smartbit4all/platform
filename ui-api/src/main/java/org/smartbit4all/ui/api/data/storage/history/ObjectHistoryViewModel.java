package org.smartbit4all.ui.api.data.storage.history;

import java.net.URI;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

public interface ObjectHistoryViewModel extends ObjectEditing {
  
  @PublishEvents("OBJECT_HISTORY")
  ObservableObject objectHistory();
  
  @NotifyListeners
  void setObjectHistoryRef(URI objectUri, String scheme);
}
