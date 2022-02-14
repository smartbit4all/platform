package org.smartbit4all.ui.api.viewmodel;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

public interface ViewModel extends ObjectEditing {

  @PublishEvents("DATA")
  ObservableObject data();

  // TODO Commands?
  // @PublishEvents("COMMANDS")
  // ObservableObject commands();

  void init(URI objectUri);

  void initByUUID(UUID uuid);

  void onCloseWindow();

  // TODO finish, finishEditing?

}
