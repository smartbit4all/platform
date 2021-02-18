package org.smartbit4all.api.object;

public interface ObjectEditing {

  @NotifyListeners
  void setValue(String propertyPath, Object value);

  @NotifyListeners
  void addValue(String collectionPath, Object value);

  @NotifyListeners
  void removeValue(String collectionElementPath);

  @PublishEvents("OBJECT")
  ObjectPublisher publisher();
}
