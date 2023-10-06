package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

  StoredListStorageImpl(String storageSchema, URI uri, String name, URI scopeUri,
      ObjectApi objectApi,
      BranchApi branchApi) {
    super(new StoredCollectionDescriptor().schema(storageSchema).name(name).scopeUri(scopeUri)
        .collectionType(CollectionTypeEnum.LIST), uri);
    this.objectApi = objectApi;
    this.branchApi = branchApi;
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
        uris.forEach(data::addUrisItem);
        // TODO add the new object to the branch entry.
        if (branchUri != null) {

        }
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
  public void removeAll(Collection<URI> uris) {
    modifyOnBranch(on -> {
      on.modify(StoredListData.class, data -> {
        data.getUris().removeAll(uris);
        return data;
      });
    });
  }

  @Override
  public void remove(URI uri) {
    modifyOnBranch(on -> {
      on.modify(StoredListData.class, data -> {
        data.getUris().remove(uri);
        return data;
      });
    });
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

}
