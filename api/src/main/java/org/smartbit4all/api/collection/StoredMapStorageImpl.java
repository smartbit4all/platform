package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor.CollectionTypeEnum;
import org.smartbit4all.api.collection.bean.StoredMapData;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;

/**
 * @author Peter Boros
 *
 */
public class StoredMapStorageImpl extends AbstractStoredContainerStorageImpl implements StoredMap {

  StoredMapStorageImpl(String storageSchema, URI uri, String name, URI scopeUri,
      ObjectApi objectApi,
      BranchApi branchApi) {
    super(new StoredCollectionDescriptor().schema(storageSchema).name(name).scopeUri(scopeUri)
        .collectionType(CollectionTypeEnum.MAP), uri);
    this.objectApi = objectApi;
    this.branchApi = branchApi;
  }

  @Override
  public Map<String, URI> uris() {
    try {
      ObjectNode objectNode = objectApi.loadLatest(uri, branchUri);
      StoredMapData data = objectNode.getObject(StoredMapData.class);
      return data.getUris();
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
    modifyOnBranch(on -> {
      on.modify(StoredMapData.class, data -> {
        values.forEach(v -> {
          data.putUrisItem(v.getKey(), v.getValue());
        });
        return data;
      });
    });
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
    modifyOnBranch(on -> {
      on.modify(StoredMapData.class, data -> {
        keys.forEach(v -> {
          data.getUris().remove(v);
        });
        return data;
      });
    });
  }

  @Override
  public Map<String, URI> update(UnaryOperator<Map<String, URI>> update) {
    Map<String, URI> result = new HashMap<>();
    modifyOnBranch(on -> {
      on.modify(StoredMapData.class, data -> {
        Map<String, URI> original = getFromData(data);
        Map<String, URI> copyOfOriginal = new HashMap<>(original);
        Map<String, URI> updated = update.apply(original);
        if (updated == null || Objects.equals(copyOfOriginal, updated)) {
          result.putAll(copyOfOriginal);
          return null;
        }
        result.putAll(updated);
        return data.uris(result);
      });
    });
    return result;
  }

  private Map<String, URI> getFromData(StoredMapData data) {
    return data == null || data.getUris() == null ? new HashMap<>() : data.getUris();
  }

  @Override
  protected ObjectNode constructNew(URI uri) {
    return objectApi.create(descriptor.getSchema(),
        new StoredMapData().uri(uri).name(descriptor.getName()));
  }

}
