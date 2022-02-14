package org.smartbit4all.ui.api.userselector;

import java.net.URI;
import java.util.List;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;

public interface UserSelectorViewModel extends ObjectEditing {

  public static final String CLOSE_CMD = "CLOSE_SELECTOR_CMD";
  public static final String SAVE_CMD = "SAVE_SELECTOR_CMD";

  @PublishEvents("SINGLESELECTOR")
  ObservableObject singleSelector();

  @PublishEvents("MULTISELECTOR")
  ObservableObject multiSelector();

  @PublishEvents("COMMANDS")
  ObservableObject commands();

  @NotifyListeners
  void setSingleSelectorRef(URI selectedUserUri);

  @NotifyListeners
  void setMultiSelectorRef(List<URI> selectedUserUris);

  @NotifyListeners
  void executeCommand(String code, String... param);

  @NotifyListeners
  void removeCommand(String commandCode);
}
