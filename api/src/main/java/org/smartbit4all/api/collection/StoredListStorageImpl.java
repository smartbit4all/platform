package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.bean.StoredListData;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectLock;

public class StoredListStorageImpl extends AbstractStoredContainerStorageImpl
    implements StoredList {

  public StoredListStorageImpl(Storage storage, URI uri, String name) {
    super(storage, uri, name);
  }

  @Override
  public List<URI> uris() {
    Storage storage = storageRef.get();
    try {
      StoredListData mapData = storage.read(uri, StoredListData.class);
      return mapData.getUris();
    } catch (ObjectNotFoundException e) {
      return Collections.emptyList();
    }
  }

  @Override
  public void add(URI uri) {
    addAll(Stream.of(uri));
  }

  @Override
  public void addAll(Collection<URI> uris) {
    if (uris != null) {
      addAll(uris.stream());
    }
  }

  @Override
  public void addAll(Stream<URI> uris) {
    Storage storage = storageRef.get();
    StorageObjectLock lock = storage.getLock(uri);
    lock.lock();
    try {
      try {
        StorageObject<StoredListData> so = storage.load(uri, StoredListData.class).asMap();
        StoredListData data = so.getObject();
        uris.forEach(u -> {
          data.addUrisItem(u);
        });
        so.setObject(data);
        storage.save(so);
      } catch (ObjectNotFoundException e) {
        StoredListData data = new StoredListData().name(name).uri(uri);
        uris.forEach(u -> data.addUrisItem(u));
        storage.saveAsNewObject(data);
      }
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void update(UnaryOperator<List<URI>> update) {
    Storage storage = storageRef.get();
    StorageObjectLock lock = storage.getLock(uri);
    lock.lock();
    try {
      try {
        StorageObject<StoredListData> so = storage.load(uri, StoredListData.class).asMap();
        StoredListData data = so.getObject();
        data.setUris(update.apply(data.getUris()));
        so.setObject(data);
        storage.save(so);
      } catch (ObjectNotFoundException e) {
        StoredListData data = new StoredListData().name(name).uri(uri);
        data.setUris(update.apply(data.getUris()));
        storage.saveAsNewObject(data);
      }
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void removeAll(Collection<URI> uris) {
    update(l -> {
      l.removeAll(uris);
      return l;
    });
  }

  @Override
  public void remove(URI uri) {
    update(l -> {
      l.remove(uri);
      return l;
    });
  }

}
