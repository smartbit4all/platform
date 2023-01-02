package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.collection.bean.StoredReferenceData;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObjectLock;

public class StoredReferenceStorageImpl<T> extends AbstractStoredContainerStorageImpl
    implements StoredReference<T> {

  private ObjectDefinition<T> def;

  protected StoredReferenceStorageImpl(Storage storage, URI uri, String name,
      ObjectDefinition<T> def) {
    super(storage, uri, name);
    this.def = def;
  }

  @Override
  public void set(T object) {
    update(o -> object);
  }

  @Override
  public void update(UnaryOperator<T> update) {
    Storage storage = storageRef.get();
    StorageObjectLock objectLock = storage.getLock(uri);
    objectLock.lock();
    try {
      if (storage.exists(uri)) {
        storage.update(uri, StoredReferenceData.class, r -> {
          return r.refObject(update.apply(getFromData(r)));
        });
      } else {
        storage.saveAsNew(new StoredReferenceData().uri(uri).refObject(update.apply(null)));
      }
    } finally {
      objectLock.unlock();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public T get() {
    Storage storage = storageRef.get();
    try {
      return getFromData(storage.read(uri, StoredReferenceData.class));
    } catch (ObjectNotFoundException e) {
      return null;
    }
  }

  private T getFromData(StoredReferenceData referenceData) {
    if (referenceData.getRefObject() == null) {
      return null;
    }
    return def.fromMap((Map<String, Object>) referenceData.getRefObject());
  }

  @Override
  public void clear() {
    set(null);
  }

}
