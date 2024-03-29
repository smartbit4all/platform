package org.smartbit4all.ui.api.viewmodel;

import java.util.UUID;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.navigation.model.ViewModelData;

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

  void addChild(ViewModel child, String path, NavigationTarget navigationTarget);

  <V extends ViewModel> V getChild(String path);

  ViewModelData getViewModelData();

  BinaryData getDownloadData(String identifier);

  UUID getUuid();

  // TODO finish, finishEditing?

}
