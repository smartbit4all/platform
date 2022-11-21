package org.smartbit4all.core.object;

import java.util.Optional;

public interface DomainObjectRef {

  String getPath();

  Optional<ObjectChange> renderAndCleanChanges();

  DomainObjectRef getValueRefByPath(String path);

  Object getValueByPath(String path);

  void setValueByPath(String path, Object value);

  DomainObjectRef addValueByPath(String path, Object value);

  void removeValueByPath(String path);

  void setObject(Object loadedObject);

  Object getObject();

  <T> T getWrapper(Class<T> beanClass);

  void reevaluateChanges();

  Object getValue(String propertyName);
}
