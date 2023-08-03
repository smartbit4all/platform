package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor.CollectionTypeEnum;
import org.smartbit4all.api.collection.bean.StoredReferenceData;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.storage.ObjectNotFoundException;

public class StoredReferenceStorageImpl<T> extends AbstractStoredContainerStorageImpl
    implements StoredReference<T> {

  private ObjectDefinition<T> def;

  protected StoredReferenceStorageImpl(String storageSchema, URI uri, String name, URI scopeUri,
      ObjectDefinition<T> def, ObjectApi objectApi,
      BranchApi branchApi) {
    super(new StoredCollectionDescriptor().schema(storageSchema).name(name).scopeUri(scopeUri)
        .collectionType(CollectionTypeEnum.REFERENCE), uri);
    this.def = def;
    this.objectApi = objectApi;
    this.branchApi = branchApi;
  }

  @Override
  public void set(T object) {
    update(o -> object);
  }

  @Override
  protected ObjectNode constructNew(URI uri) {
    return objectApi.create(descriptor.getSchema(), new StoredReferenceData().uri(uri));
  }

  @Override
  public void update(UnaryOperator<T> update) {
    modifyOnBranch(on -> {
      on.modify(StoredReferenceData.class, data -> {
        return data.refObject(update.apply(getFromData(data)));
      });
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public T get() {
    ObjectNode objectNode = objectApi.loadLatest(uri, branchUri);
    try {
      return getFromData(objectNode.getObject(StoredReferenceData.class));
    } catch (ObjectNotFoundException e) {
      return null;
    }
  }

  @Override
  public boolean exists() {
    return objectApi.exists(getUri(), branchUri);
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
