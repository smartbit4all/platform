package org.smartbit4all.ui.api.viewmodel;

import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;

public interface ObjectEditing {

  static final ThreadLocal<NavigationTarget> currentNavigationTarget = new ThreadLocal<>();

  @NotifyListeners
  void executeCommand(String path, String command, Object... params);

}
