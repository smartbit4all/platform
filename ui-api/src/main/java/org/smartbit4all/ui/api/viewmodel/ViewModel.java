package org.smartbit4all.ui.api.viewmodel;

import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;

public interface ViewModel extends ObjectEditing {

  @PublishEvents("DATA")
  ObservableObject data();

  // TODO Commands?
  // @PublishEvents("COMMANDS")
  // ObservableObject commands();

  /**
   * Called by the View when UI is loaded and receives it's URL parameter. It will initialize this
   * view model with specified navigationTarget. Default implementation will load object by
   * navigationTarget, initialize ref and subscription.
   * 
   * @param navigationTarget
   */
  void initByNavigationTarget(NavigationTarget navigationTarget);

  void onCloseWindow();

  void addChild(ViewModel child, String path);

  // TODO finish, finishEditing?

}
