package org.smartbit4all.core.object;

import org.smartbit4all.core.constraint.ObjectConstraintPublisher;

public class ObjectEditingImpl implements ObjectEditing {

  protected ApiObjectRef ref;

  protected ObjectPublisherImpl publisher;

  protected ObjectConstraintPublisher constraints;

  @Override
  public void setValue(String propertyPath, Object value) {
    ref.setValueByPath(propertyPath, value);
  }

  @Override
  public void addValue(String collectionPath, Object value) {
    ref.addValueByPath(collectionPath, value);
  }

  @Override
  public void removeValue(String collectionElementPath) {
    ref.removeValueByPath(collectionElementPath);
  }

  @Override
  public ObjectPublisher publisher() {
    return publisher;
  }

  @Override
  public ObjectConstraintPublisher constraints() {
    return constraints;
  }

}
