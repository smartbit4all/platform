package org.smartbit4all.api.collection;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor.CollectionTypeEnum;
import org.smartbit4all.api.collection.bean.StoredListData;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;

public class StoredListStorageImpl extends AbstractStoredContainerStorageImpl
    implements StoredList {

  private OperationMode operationMode = OperationMode.NORMAL;

  private StoredListCacheEntry cacheEntry;

  StoredListStorageImpl(String storageSchema, URI uri, String name, URI scopeUri,
      ObjectApi objectApi,
      BranchApi branchApi,
      StoredListCacheEntry cacheEntry) {
    super(new StoredCollectionDescriptor().schema(storageSchema).name(name).scopeUri(scopeUri)
        .collectionType(CollectionTypeEnum.LIST), uri);
    this.objectApi = objectApi;
    this.branchApi = branchApi;
    this.cacheEntry = cacheEntry;
  }

  @Override
  public List<URI> uris() {
    try {
      ObjectNode objectNode = objectApi.loadLatest(uri, branchUri);
      StoredListData data = objectNode.getObject(StoredListData.class);
      return data.getUris();
    } catch (ObjectNotFoundException e) {
      return Collections.emptyList();
    }
  }

  @Override
  public Stream<ObjectNode> nodesFromCache() {
    if (!objectApi.exists(uri)) {
      return Stream.empty();
    }
    return cacheEntry.cacheRef.updateAndGet(cache -> {
      if (cache == null || objectApi.getLastModified(uri) > cacheEntry.lastCacheRefreshmentTime) {
        cacheEntry.lastCacheRefreshmentTime = System.currentTimeMillis();
        return nodes().collect(toList());
      }
      return cache;
    }).stream();
  }

  @Override
  public Stream<ObjectNode> nodes() {
    return uris().stream().map(u -> objectApi.load(u));
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
    modifyOnBranch(on -> {
      on.modify(StoredListData.class, data -> {
        Set<URI> currentUris = new HashSet<>();
        if (operationMode != OperationMode.NORMAL) {
          if (data.getUris() != null) {
            currentUris.addAll(data.getUris().stream().map(u -> {
              if (operationMode == OperationMode.UNIQUE) {
                return u;
              }
              return objectApi.getLatestUri(u);
            }).collect(toList()));
          }
        }
        uris.filter(u -> {
          if (!currentUris.isEmpty()) {
            if (operationMode == OperationMode.UNIQUE) {
              return !currentUris.contains(u);
            }
            return !currentUris.contains(objectApi.getLatestUri(u));
          }
          return true;
        }).forEach(data::addUrisItem);
        return data;
      });
    });
  }

  @Override
  public List<URI> update(UnaryOperator<List<URI>> update) {
    List<URI> result = new ArrayList<>();
    modifyOnBranch(on -> {
      on.modify(StoredListData.class, data -> {
        result.addAll(update.apply(data.getUris()));
        data.setUris(result);
        return data;
      });
    });
    return result;
  }

  @Override
  public boolean removeAll(Collection<URI> uris) {
    List<URI> removedUris = new ArrayList<>();
    modifyOnBranch(on -> {
      on.modify(StoredListData.class, data -> {
        if (data.getUris().removeAll(uris)) {
          removedUris.addAll(uris);
        }
        return data;
      });
    });
    return !removedUris.isEmpty();
  }

  @Override
  public boolean remove(URI uri) {
    List<URI> removedUris = new ArrayList<>();
    modifyOnBranch(on -> {
      on.modify(StoredListData.class, data -> {
        if (branchUri != null) {
          if (data.getUris().removeIf(u -> objectApi.equalsIgnoreVersion(u, uri))) {
            removedUris.add(uri);
          }
        } else {
          if (data.getUris().remove(uri)) {
            removedUris.add(uri);
          }
        }
        return data;
      });
    });
    return !removedUris.isEmpty();
  }

  @Override
  public void addOrMoveFirst(URI uri, int maxSize, boolean assumeLatestUri) {
    modifyOnBranch(on -> {
      on.modify(StoredListData.class, data -> {
        URI uriToAdd = assumeLatestUri ? ObjectStorageImpl.getUriWithoutVersion(uri) : uri;
        // Remove if exists
        List<URI> uris = data.getUris();
        uris.remove(uriToAdd);
        // Add at first position
        uris.add(0, uriToAdd);
        data.setUris(uris.stream().distinct().collect(Collectors.toList()));
        // Retrieve again.
        uris = data.getUris();
        if (uris.size() > maxSize) {
          uris.remove(uris.size() - 1);
        }
        return data;
      });
    });
  }


  @Override
  protected ObjectNode constructNew(URI uri) {
    return objectApi.create(descriptor.getSchema(),
        new StoredListData().uri(uri).name(descriptor.getName()));
  }

  @Override
  public List<BranchedObjectEntry> compareWithBranch(URI branchUri) {
    return branchApi.compareListByUri(branchUri, getUri(), StoredListData.URIS);
  }

  @Override
  public StoredList operationMode(OperationMode mode) {
    operationMode = mode;
    return this;
  }

}
