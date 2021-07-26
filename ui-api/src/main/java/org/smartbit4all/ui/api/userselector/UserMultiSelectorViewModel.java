package org.smartbit4all.ui.api.userselector;

import java.net.URI;
import java.util.List;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

public interface UserMultiSelectorViewModel extends ObjectEditing {
  
  public static final String CLOSE_CMD = "CLOSE_MULTI_SELECTOR_CMD";
  public static final String SAVE_CMD = "SAVE_MULTI_SELECTOR_CMD";
  
  @PublishEvents("OBJECT")
  ObservableObject userMultiSelector();
  
  @NotifyListeners
  void initUserMultiSelectors(List<URI> selected);
  
  @NotifyListeners
  public void executeCommand(String code, String... param) throws Throwable;
  
  void initObservableObject();
  
  void init();
}
