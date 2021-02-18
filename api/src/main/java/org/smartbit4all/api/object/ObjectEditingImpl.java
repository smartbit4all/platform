package org.smartbit4all.api.object;

public class ObjectEditingImpl implements ObjectEditing{

  protected ApiObjectRef ref;

  protected ObjectPublisherImpl publisher;

  @NotifyListeners
  @Override
  public void setValue(String propertyPath, Object value) {
    ref.setValueByPath(propertyPath, value);
  }

  @NotifyListeners
  @Override
  public void addValue(String collectionPath, Object value) {
    ref.addValueByPath(collectionPath, value);
  }

  @NotifyListeners
  @Override
  public void removeValue(String collectionElementPath) {
    ref.removeValueByPath(collectionElementPath);
  }

  @Override
  public ObjectPublisher publisher() {
    return publisher;
  }

}
