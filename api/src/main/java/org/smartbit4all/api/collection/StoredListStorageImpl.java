package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.bean.StoredListData;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectLock;

public class StoredListStorageImpl extends AbstractStoredContainerStorageImpl
    implements StoredList {

  private ObjectApi objectApi;

  private BranchApi branchApi;

  StoredListStorageImpl(Storage storage, URI uri, String name, ObjectApi objectApi,
      BranchApi branchApi) {
    super(storage, uri, name);
    this.objectApi = objectApi;
    this.branchApi = branchApi;
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
  public boolean exists() {
    return storageRef.get().exists(getUri());
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
  public List<URI> update(UnaryOperator<List<URI>> update) {
    Storage storage = storageRef.get();
    StorageObjectLock lock = storage.getLock(uri);
    List<URI> result = Collections.emptyList();
    lock.lock();
    try {
      try {
        StorageObject<StoredListData> so = storage.load(uri, StoredListData.class).asMap();
        StoredListData data = so.getObject();
        result = update.apply(data.getUris());
        data.setUris(result);
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
    return result;
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

  @Override
  public void addOrMoveFirst(URI uri, int maxSize, boolean assumeLatestUri) {
    update(l -> {
      URI uriToAdd = assumeLatestUri ? ObjectStorageImpl.getUriWithoutVersion(uri) : uri;
      // Remove if exists
      l.remove(uriToAdd);
      // Add at first position
      l.add(0, uriToAdd);
      l = l.stream().distinct().collect(Collectors.toList());
      if (l.size() > maxSize) {
        l.remove(l.size() - 1);
      }
      return l;
    });
  }


  @Override
  public StoredList makeBranch(URI branchUri) {
    this.branchUri = branchUri;
    Lock lock = objectApi.getLock(uri);
    lock.lock();
    try {
      ObjectNode storedListNode = objectApi.loadLatest(uri);
      objectApi.save(storedListNode, branchUri);
    } finally {
      lock.unlock();
    }
    return this;
  }

}
