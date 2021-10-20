package org.smartbit4all.core.object;

import java.util.UUID;
import org.smartbit4all.core.constraint.ObjectConstraintPublisher;

public interface ObjectEditing {

  static final ThreadLocal<UUID> currentConstructionUUID = new ThreadLocal<>();

  @PublishEvents("CONSTRAINTS")
  ObjectConstraintPublisher constraints();

  @NotifyListeners
  void executeCommand(String path, String command, Object... params);

}
