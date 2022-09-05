package org.smartbit4all.domain.data.storage;

import java.util.Map;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.core.object.ObjectDefinition;

public class StorageObjectHistoryEntry {

  private ObjectVersion version;

  private Object object;

  private Map<String, Object> objectAsMap;

  public StorageObjectHistoryEntry(ObjectVersion version, Map<String, Object> objectAsMap) {
    super();
    this.version = version;
    this.objectAsMap = objectAsMap;
  }

  public final ObjectVersion getVersion() {
    return version;
  }

  public final void setVersion(ObjectVersion version) {
    this.version = version;
  }

  public final Object getObject(ObjectDefinition<?> definition) {
    if (object == null) {
      object = definition.fromMap(objectAsMap);
    }
    return object;
  }

  public final Map<String, Object> getObjectAsMap() {
    return objectAsMap;
  }

  public final StorageObjectHistoryEntry setObjectAsMap(Map<String, Object> objectAsMap) {
    this.objectAsMap = objectAsMap;
    return this;
  }

}
