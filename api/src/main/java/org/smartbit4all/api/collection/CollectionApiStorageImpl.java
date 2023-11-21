package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The {@link StorageApi} based implementation of the {@link CollectionApi} is currently the only
 * one but later on these collections can be contributed with many underlying implementation.
 * 
 * @author Peter Boros
 */
public class CollectionApiStorageImpl implements CollectionApi, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(CollectionApiStorageImpl.class);

  public static final String STOREDMAP = "storedmap";
  public static final String STOREDLIST = "storedlist";
  public static final String STOREDREF = "storedRef";
  public static final String STOREDSEQ = "storedSeq";

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private BranchApi branchApi;

  @Autowired
  private StorageSequenceApi sequenceApi;

  /**
   * This map contains the already used {@link Storage} instances mapped by the schema name.
   */
  private Map<String, Storage> storagesBySchema = new HashMap<>();

  @Autowired(required = false)
  private List<SearchIndex<?>> searchIndices;

  private Map<String, SearchIndex<?>> searchIndexByName = new HashMap<>();

  private Cache<String, StoredListCacheEntry> listCacheEntries = CacheBuilder.newBuilder().build();

  public CollectionApiStorageImpl() {
    super();
  }

  @Override
  public StoredMap map(String logicalSchema, String mapName) {
    return new StoredMapStorageImpl(constructCollectionShemaName(logicalSchema),
        constructGlobalUri(constructCollectionShemaName(logicalSchema), mapName, STOREDMAP),
        mapName, null, objectApi,
        branchApi);
  }

  @Override
  public StoredMap map(URI scopeObjectUri, String logicalSchema, String mapName) {
    return new StoredMapStorageImpl(constructCollectionShemaName(logicalSchema),
        constructScopedUri(constructCollectionShemaName(logicalSchema),
            mapName, ObjectStorageImpl.getUriWithoutVersion(scopeObjectUri), STOREDMAP),
        mapName, scopeObjectUri,
        objectApi,
        branchApi);
  }

  @Override
  public StoredList list(String logicalSchema, String name) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredListStorageImpl(schema, constructGlobalUri(schema, name, STOREDLIST),
        name, null, objectApi,
        branchApi, getListCacheEntry(logicalSchema, name));
  }

  @Override
  public StoredList list(URI scopeObjectUri, String logicalSchema, String name) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredListStorageImpl(schema, constructScopedUri(schema,
        name, ObjectStorageImpl.getUriWithoutVersion(scopeObjectUri), STOREDLIST), name,
        scopeObjectUri, objectApi,
        branchApi, getListCacheEntry(logicalSchema, name));
  }

  @Override
  public StoredList list(StoredCollectionDescriptor descriptor) {
    return descriptor.getScopeUri() == null ? list(descriptor.getSchema(), descriptor.getName())
        : list(descriptor.getScopeUri(), descriptor.getSchema(), descriptor.getName());
  }

  @Override
  public <T> StoredReference<T> reference(String logicalSchema, String name, Class<T> clazz) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredReferenceStorageImpl<>(schema,
        constructGlobalUri(schema, name, STOREDREF), name, null, objectApi.definition(clazz),
        objectApi,
        branchApi);
  }

  @Override
  public <T> StoredReference<T> reference(URI scopeObjectUri, String logicalSchema, String name,
      Class<T> clazz) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredReferenceStorageImpl<>(schema,
        constructScopedUri(schema,
            name, ObjectStorageImpl.getUriWithoutVersion(scopeObjectUri), STOREDREF),
        name, scopeObjectUri, objectApi.definition(clazz), objectApi, branchApi);
  }

  @Override
  public <T> StoredReference<T> reference(URI refUri, Class<T> clazz) {
    Storage storage = storageApi.getStorage(refUri);
    String logicalSchema = storage.getScheme();
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredReferenceStorageImpl<>(schema,
        refUri,
        null, null, objectApi.definition(clazz), objectApi, branchApi);
  }

  @Override
  public <O> SearchIndex<O> searchIndex(String logicalSchema, String name, Class<O> indexedObject) {
    @SuppressWarnings("unchecked")
    SearchIndex<O> result =
        (SearchIndex<O>) searchIndexByName.get(getQualifiedName(logicalSchema, name));
    Objects.requireNonNull(result, "The " + name + " search index is not available.");
    return result;
  }

  @Override
  public SearchIndex<?> searchIndex(String logicalSchema, String name) {
    SearchIndex<?> result =
        searchIndexByName.get(getQualifiedName(logicalSchema, name));
    Objects.requireNonNull(result, "The " + name + " search index is not available.");
    return result;
  }

  @Override
  public <O, F> SearchIndexWithFilterBean<O, F> searchIndex(String logicalSchema, String name,
      Class<O> indexedObject, Class<F> filterObject) {
    @SuppressWarnings("unchecked")
    SearchIndexWithFilterBean<O, F> result =
        (SearchIndexWithFilterBean<O, F>) searchIndexByName
            .get(logicalSchema + StringConstant.DOT + name);
    Objects.requireNonNull(result, "The " + name + " search index is not available.");
    return result;
  }

  @Override
  public StoredSequence sequence(String logicalSchema, String name) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredSequenceStorageImpl(constructGlobalUri(schema, name, STOREDSEQ), name,
        sequenceApi);
  }

  /**
   * This function produce the given logical schema. This schema is currently non versioned by
   * default.
   * 
   * @param logicalSchema
   * @return
   */
  private final synchronized Storage setupStorage(String logicalSchema) {
    return storagesBySchema.computeIfAbsent(logicalSchema,
        s -> storageApi.get(s).setVersionPolicy(VersionPolicy.SINGLEVERSION));
  }

  public static final URI constructGlobalUri(String logicalSchema, String mapName, String kind) {
    return UriUtils.createUri(logicalSchema, null,
        StringConstant.SLASH + kind + StringConstant.SLASH + mapName
            + Storage.SINGLE_VERSION_URI_POSTFIX,
        null);
  }

  public static final URI constructScopedUri(String logicalSchema, String mapName,
      URI uriWithoutVersion,
      String kind) {
    return UriUtils.createUri(logicalSchema, null,
        uriWithoutVersion.getPath() + StringConstant.SLASH + kind + StringConstant.SLASH
            + mapName
            + Storage.SINGLE_VERSION_URI_POSTFIX,
        null);
  }

  private final String constructCollectionShemaName(String logicalShema) {
    String result = logicalShema + StringConstant.MINUS_SIGN + "collections";
    setupStorage(result);
    return result;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (searchIndices == null) {
      return;
    }

    for (SearchIndex<?> searchIndex : searchIndices) {
      searchIndexByName.put(
          getQualifiedName(searchIndex.logicalSchema(), searchIndex.name()),
          searchIndex);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> SearchIndex<T> searchIndexComputeIfAbsent(String logicalSchema, String name,
      Supplier<SearchIndex<T>> searchIndexSupplier, Class<T> clazz) {
    return (SearchIndex<T>) searchIndexByName.computeIfAbsent(
        getQualifiedName(logicalSchema, name),
        s -> (SearchIndex<T>) searchIndexSupplier.get());
  }

  private final String getQualifiedName(String logicalSchema, String name) {
    return logicalSchema + StringConstant.DOT + name;
  }

  private final StoredListCacheEntry getListCacheEntry(String logicalSchema, String name) {
    StoredListCacheEntry cacheEntry;
    try {
      cacheEntry = listCacheEntries.get(getQualifiedName(logicalSchema, name),
          StoredListCacheEntry::new);
    } catch (Exception e) {
      log.error("Unable to initiate cache entry.", e);
      cacheEntry = new StoredListCacheEntry();
    }
    return cacheEntry;
  }

}
