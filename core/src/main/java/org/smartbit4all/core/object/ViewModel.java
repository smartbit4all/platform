package org.smartbit4all.core.object;

import java.net.URI;

public interface ViewModel extends ObjectEditing {

  @PublishEvents("DATA")
  ObservableObject data();

  // TODO Commands?
  // @PublishEvents("COMMANDS")
  // ObservableObject commands();

  void init(URI objectUri);

  void onCloseWindow();

  // TODO finish, finishEditing?

}
