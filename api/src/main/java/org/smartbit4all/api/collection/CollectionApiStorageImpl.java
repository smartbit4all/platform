package org.smartbit4all.api.collection;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor.CollectionTypeEnum;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The {@link StorageApi} based implementation of the {@link CollectionApi} is currently the only
 * one but later on these collections can be contributed with many underlying implementation.
 *
 * @author Peter Boros
 */
public class CollectionApiStorageImpl implements CollectionApi {

  private static final Logger log = LoggerFactory.getLogger(CollectionApiStorageImpl.class);

  public static final String STOREDMAP = "storedmap";
  public static final String STOREDLIST = "storedlist";
  public static final String STOREDREF = "storedRef";

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private BranchApi branchApi;

  @Autowired
  private VectorDBApi vectorDBApi;

  @Autowired
  private EmbeddingApi embeddingApi;

  /**
   * This map contains the already used {@link Storage} instances mapped by the schema name.
   */
  private Map<String, Storage> storagesBySchema = new HashMap<>();

  @Autowired
  private ApplicationContext ctx;

  private boolean searchIndexesInitialized = false;

  private Map<String, SearchIndex<?>> searchIndexByName = new HashMap<>();

  private Cache<String, StoredListCacheEntry> listCacheEntries = CacheBuilder.newBuilder().build();

  public CollectionApiStorageImpl() {
    super();
  }

  @Override
  public StoredMap map(String logicalSchema, String mapName) {
    return new StoredMapStorageImpl(logicalSchema, constructCollectionShemaName(logicalSchema),
        constructGlobalUri(constructCollectionShemaName(logicalSchema), mapName, STOREDMAP),
        mapName, null, objectApi,
        branchApi);
  }

  @Override
  public StoredMap map(URI scopeObjectUri, String logicalSchema, String mapName) {
    return new StoredMapStorageImpl(logicalSchema, constructCollectionShemaName(logicalSchema),
        constructScopedUri(constructCollectionShemaName(logicalSchema),
            mapName, ObjectStorageImpl.getUriWithoutVersion(scopeObjectUri), STOREDMAP),
        mapName, scopeObjectUri,
        objectApi,
        branchApi);
  }

  @Override
  public StoredMap map(StoredCollectionDescriptor descriptor) {
    return descriptor.getScopeUri() == null ? map(descriptor.getSchema(), descriptor.getName())
        : map(descriptor.getScopeUri(), descriptor.getSchema(), descriptor.getName());
  }

  @Override
  public StoredList list(String logicalSchema, String name) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredListStorageImpl(logicalSchema, schema,
        constructGlobalUri(schema, name, STOREDLIST),
        name, null, objectApi,
        branchApi, getListCacheEntry(logicalSchema, name));
  }

  @Override
  public StoredList list(URI scopeObjectUri, String logicalSchema, String name) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredListStorageImpl(logicalSchema, schema, constructScopedUri(schema,
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
  public StoredContainer container(StoredCollectionDescriptor descriptor) {
    if (descriptor == null) {
      return null;
    }
    if (descriptor.getCollectionType() == CollectionTypeEnum.LIST) {
      return list(descriptor);
    } else if (descriptor.getCollectionType() == CollectionTypeEnum.MAP) {
      return map(descriptor);
    }
    return null;
  }

  @Override
  public List<Lock> lockAll(List<StoredCollectionDescriptor> collections) {
    Objects.requireNonNull(collections);
    List<URI> uris = collections.stream()
        .map(c -> {
          return c.getScopeUri() == null
              ? constructGlobalUri(c.getSchema(), c.getName(), kindOf(c), c.getSingleVersion())
              : constructScopedUri(c.getSchema(), c.getName(), c.getScopeUri(),
                  kindOf(c), c.getSingleVersion());
        }).collect(toList());
    return objectApi.lockAll(uris);
  }

  private final String kindOf(StoredCollectionDescriptor c) {
    if (c.getCollectionType() == CollectionTypeEnum.LIST) {
      return STOREDLIST;
    } else if (c.getCollectionType() == CollectionTypeEnum.MAP) {
      return STOREDMAP;
    } else if (c.getCollectionType() == CollectionTypeEnum.REFERENCE) {
      return STOREDREF;
    } else {
      return null;
    }
  }

  @Override
  public <T> StoredReference<T> reference(String logicalSchema, String name, Class<T> clazz) {
    return referenceInner(logicalSchema, name, clazz, true);
  }

  @Override
  public <T> StoredReference<T> reference(URI scopeObjectUri, String logicalSchema, String name,
      Class<T> clazz) {
    return referenceInner(scopeObjectUri, logicalSchema, name, clazz, true);
  }

  @Override
  public <T> StoredReference<T> reference(URI refUri, Class<T> clazz) {
    return referenceInner(refUri, clazz, true);
  }

  @Override
  public <T> StoredReference<T> referenceVersioned(String logicalSchema, String name,
      Class<T> clazz) {
    return referenceInner(logicalSchema, name, clazz, false);
  }

  @Override
  public <T> StoredReference<T> referenceVersioned(URI scopeObjectUri, String logicalSchema,
      String name,
      Class<T> clazz) {
    return referenceInner(scopeObjectUri, logicalSchema, name, clazz, false);
  }

  @Override
  public <T> StoredReference<T> referenceVersioned(URI refUri, Class<T> clazz) {
    return referenceInner(refUri, clazz, false);
  }


  private final <T> StoredReference<T> referenceInner(String logicalSchema, String name,
      Class<T> clazz,
      boolean singleVersion) {
    String schema = constructCollectionShemaName(logicalSchema, singleVersion);
    return new StoredReferenceStorageImpl<>(logicalSchema, schema,
        constructGlobalUri(schema, name, STOREDREF, singleVersion), name, null,
        objectApi.definition(clazz),
        objectApi,
        branchApi);
  }

  private final <T> StoredReference<T> referenceInner(URI scopeObjectUri, String logicalSchema,
      String name, Class<T> clazz, boolean singleVersion) {
    String schema = constructCollectionShemaName(logicalSchema, singleVersion);
    return new StoredReferenceStorageImpl<>(logicalSchema, schema,
        constructScopedUri(schema,
            name, ObjectStorageImpl.getUriWithoutVersion(scopeObjectUri), STOREDREF, singleVersion),
        name, scopeObjectUri, objectApi.definition(clazz), objectApi, branchApi);
  }

  private final <T> StoredReference<T> referenceInner(URI refUri, Class<T> clazz,
      boolean singleVersion) {
    Storage storage = storageApi.getStorage(refUri);
    String logicalSchema = storage.getScheme();
    String schema = constructCollectionShemaName(logicalSchema, singleVersion);
    return new StoredReferenceStorageImpl<>(logicalSchema, schema,
        refUri,
        clazz.getName(), null, objectApi.definition(clazz), objectApi, branchApi);
  }

  @Override
  public <O> SearchIndex<O> searchIndex(String logicalSchema, String name, Class<O> indexedObject) {
    initSearchIndexesFromContext();
    @SuppressWarnings("unchecked")
    SearchIndex<O> result =
        (SearchIndex<O>) searchIndexByName.get(getQualifiedName(logicalSchema, name));
    Objects.requireNonNull(result, "The " + name + " search index is not available.");
    return result;
  }

  @Override
  public SearchIndex<?> searchIndex(String logicalSchema, String name) {
    initSearchIndexesFromContext();
    SearchIndex<?> result =
        searchIndexByName.get(getQualifiedName(logicalSchema, name));
    Objects.requireNonNull(result, "The " + name + " search index is not available.");
    return result;
  }

  @Override
  public <O, F> SearchIndexWithFilterBean<O, F> searchIndex(String logicalSchema, String name,
      Class<O> indexedObject, Class<F> filterObject) {
    initSearchIndexesFromContext();
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
    return storageApi.getSequence(schema, name);
  }

  @Override
  public StoredSequence sequence(URI scopeObjectUri, String logicalSchema, String name) {
    String schema = constructCollectionShemaName(logicalSchema);
    return storageApi.getSequence(scopeObjectUri, schema, name);
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
    return constructGlobalUri(logicalSchema, mapName, kind, true);
  }

  public static final URI constructGlobalUri(String logicalSchema, String mapName, String kind,
      boolean singleVersion) {
    return UriUtils.createUri(logicalSchema, null,
        StringConstant.SLASH + kind + StringConstant.SLASH + mapName
            + (singleVersion ? Storage.SINGLE_VERSION_URI_POSTFIX : StringConstant.EMPTY),
        null);
  }

  public static final URI constructScopedUri(String logicalSchema, String mapName,
      URI uriWithoutVersion, String kind) {
    return constructScopedUri(logicalSchema, mapName, uriWithoutVersion, kind, true);
  }

  public static final URI constructScopedUri(String logicalSchema, String mapName,
      URI uriWithoutVersion, String kind, boolean singleVersion) {
    return UriUtils.createUri(logicalSchema, null,
        uriWithoutVersion.getPath() + StringConstant.SLASH + kind + StringConstant.SLASH
            + mapName + (singleVersion ? Storage.SINGLE_VERSION_URI_POSTFIX : StringConstant.EMPTY),
        null);
  }

  private final String constructCollectionShemaName(String logicalShema) {
    return constructCollectionShemaName(logicalShema, true);
  }

  private final String constructCollectionShemaName(String logicalShema, boolean singleVersion) {
    String result = logicalShema + StringConstant.MINUS_SIGN
        + (singleVersion ? "collections" : "v-collections");
    if (singleVersion) {
      setupStorage(result);
    }
    return result;
  }

  private void initSearchIndexesFromContext() {
    if (!searchIndexesInitialized) {
      ctx.getBeansOfType(SearchIndex.class).values().stream()
          .forEach(searchIndex -> searchIndexByName.put(
              getQualifiedName(searchIndex.logicalSchema(), searchIndex.name()),
              searchIndex));
      searchIndexesInitialized = true;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> SearchIndex<T> searchIndexComputeIfAbsent(String logicalSchema, String name,
      Supplier<SearchIndex<T>> searchIndexSupplier, Class<T> clazz) {
    initSearchIndexesFromContext();
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

  @Override
  public VectorCollection vectorCollection(String name, ServiceConnection vectorDBConnection,
      ServiceConnection embeddingConnection) {
    return new VectorCollectionImpl(objectApi, vectorDBApi, vectorDBConnection, embeddingApi,
        embeddingConnection, name);
  }

}
