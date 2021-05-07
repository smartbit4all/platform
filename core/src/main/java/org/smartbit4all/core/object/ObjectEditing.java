package org.smartbit4all.core.object;

import org.smartbit4all.core.constraint.ObjectConstraintPublisher;

public interface ObjectEditing {

  @NotifyListeners
  void setValue(String propertyPath, Object value);

  @NotifyListeners
  void addValue(String collectionPath, Object value);

  @NotifyListeners
  void removeValue(String collectionElementPath);

  @PublishEvents("OBJECT")
  ObjectPublisher publisher();

  @PublishEvents("CONSTRAINTS")
  ObjectConstraintPublisher constraints();
}
