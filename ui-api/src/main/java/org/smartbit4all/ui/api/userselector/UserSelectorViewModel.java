package org.smartbit4all.ui.api.userselector;

import java.net.URI;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

public interface UserSelectorViewModel extends ObjectEditing {
  
  @PublishEvents("OBJECT")
  ObservableObject userSelectors();
  
  void initObservableObject();
  
  @NotifyListeners
  void initUserSelectors(URI selectedUserUri);
}
