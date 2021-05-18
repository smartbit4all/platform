package org.smartbit4all.core.object;

import org.smartbit4all.core.constraint.ObjectConstraintPublisher;

public interface ObjectEditing {

  @PublishEvents("CONSTRAINTS")
  ObjectConstraintPublisher constraints();

  @NotifyListeners
  void executeCommand(String path, String command, Object... params);

}
