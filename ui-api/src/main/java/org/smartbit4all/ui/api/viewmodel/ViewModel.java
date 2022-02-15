package org.smartbit4all.ui.api.viewmodel;

import java.util.UUID;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

public interface ViewModel extends ObjectEditing {

  @PublishEvents("DATA")
  ObservableObject data();

  // TODO Commands?
  // @PublishEvents("COMMANDS")
  // ObservableObject commands();

  /**
   * Called by the View when UI is loaded and receives it's URL parameter. It will initialize this
   * view model if navigationTargetUUID specified during construction equals parameter UUID. Default
   * implementation will load object by navigationTarget, initialize ref and subscription.
   * 
   * @param uuid
   */
  void initByUUID(UUID uuid);

  /**
   * Used when a child view model is created.
   */
  void initByParentRef(ApiObjectRef parentRef, String path);

  void onCloseWindow();

  // TODO finish, finishEditing?

}
