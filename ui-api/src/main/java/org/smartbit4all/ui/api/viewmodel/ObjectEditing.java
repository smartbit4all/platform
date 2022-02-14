package org.smartbit4all.ui.api.viewmodel;

import org.smartbit4all.core.constraint.ObjectConstraintPublisher;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;

public interface ObjectEditing {

  static final ThreadLocal<NavigationTarget> currentConstructionUUID = new ThreadLocal<>();

  @PublishEvents("CONSTRAINTS")
  ObjectConstraintPublisher constraints();

  @NotifyListeners
  void executeCommand(String path, String command, Object... params);

}
