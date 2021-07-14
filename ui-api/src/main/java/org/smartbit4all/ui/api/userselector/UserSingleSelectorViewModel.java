package org.smartbit4all.ui.api.userselector;

import java.net.URI;
import java.util.List;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

public interface UserSingleSelectorViewModel extends ObjectEditing {
  
  @PublishEvents("OBJECT")
  ObservableObject userSingleSelector();
  
  void initObservableObject();
  
  @NotifyListeners
  void initUserSingleSelectors(URI selectedUserUri);
}
