package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.bean.StoredMapData;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectLock;

/**
 * @author Peter Boros
 *
 */
public class StoredMapStorageImpl extends AbstractStoredContainerStorageImpl implements StoredMap {

  public StoredMapStorageImpl(Storage storage, URI uri, String name) {
    super(storage, uri, name);
  }

  @Override
  public Map<String, URI> uris() {
    Storage storage = storageRef.get();
    try {
      StoredMapData mapData = storage.read(uri, StoredMapData.class);
      return mapData.getUris();
    } catch (ObjectNotFoundException e) {
      return Collections.emptyMap();
    }
  }

  @Override
  public void put(String key, URI uri) {
    put(Stream.of(new StoredMapEntry(key, uri)));
  }

  @Override
  public void putAll(Map<String, URI> values) {
    if (values != null) {
      put(values.entrySet().stream().map(e -> new StoredMapEntry(e.getKey(), e.getValue())));
    }
  }

  @Override
  public void put(Stream<StoredMapEntry> values) {
    // TODO This could be a storage basic service getOrCreate
    Storage storage = storageRef.get();
    StorageObjectLock lock = storage.getLock(uri);
    lock.lock();
    try {
      try {
        StorageObject<StoredMapData> so = storage.load(uri, StoredMapData.class).asMap();
        StoredMapData mapData = so.getObject();
        values.forEach(v -> {
          mapData.putUrisItem(v.getKey(), v.getValue());
        });
        so.setObject(mapData);
        storage.save(so);
      } catch (ObjectNotFoundException e) {
        StoredMapData mapData = new StoredMapData().name(name).uri(uri);
        values.forEach(v -> mapData.putUrisItem(v.getKey(), v.getValue()));
        storage.saveAsNewObject(mapData);
      }
    } finally {
      lock.unlockAndRelease();;
    }
  }

  @Override
  public void remove(String key) {
    if (key != null) {
      remove(Stream.of(key));
    }
  }

  @Override
  public void remove(Collection<String> keys) {
    if (keys != null) {
      remove(keys.stream());
    }
  }

  @Override
  public void remove(Stream<String> keys) {
    Storage storage = storageRef.get();
    StorageObjectLock lock = storage.getLock(uri);
    lock.lock();
    try {
      try {
        StorageObject<StoredMapData> so = storage.load(uri, StoredMapData.class).asMap();
        StoredMapData mapData = so.getObject();
        keys.forEach(k -> {
          mapData.getUris().remove(k);
        });
        so.setObject(mapData);
        storage.save(so);
      } catch (ObjectNotFoundException e) {
        // NOP. Removing from a not existing map...
      }
    } finally {
      lock.unlockAndRelease();;
    }
  }

}
