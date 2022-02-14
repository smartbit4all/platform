package org.smartbit4all.ui.api.viewmodel;

import java.util.UUID;
import org.smartbit4all.core.constraint.ObjectConstraintPublisher;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.PublishEvents;

public interface ObjectEditing {

  static final ThreadLocal<UUID> currentConstructionUUID = new ThreadLocal<>();

  @PublishEvents("CONSTRAINTS")
  ObjectConstraintPublisher constraints();

  @NotifyListeners
  void executeCommand(String path, String command, Object... params);

}
