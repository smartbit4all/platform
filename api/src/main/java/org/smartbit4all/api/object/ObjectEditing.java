package org.smartbit4all.api.object;

public interface ObjectEditing {

  @NotifiyListeners
  void setValue(String propertyPath, Object value);

  @NotifiyListeners
  void addValue(String collectionPath, Object value);

  @NotifiyListeners
  void removeValue(String collectionElementPath);

  ObjectPublisher publisher();
}
