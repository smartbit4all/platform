package org.smartbit4all.core.object;

import org.smartbit4all.core.constraint.ObjectConstraintPublisher;

public class ObjectEditingImpl implements ObjectEditing {

  protected ApiObjectRef ref;

  protected ObjectConstraintPublisher constraints;

  @Override
  public ObjectConstraintPublisher constraints() {
    return constraints;
  }

}
