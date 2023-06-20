package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toMap;

/**
 * The {@link StorageApi} based implementation of the {@link CollectionApi} is currently the only
 * one but later on these collections can be contributed with many underlying implementation.
 * 
 * @author Peter Boros
 */
public class CollectionApiStorageImpl implements CollectionApi, InitializingBean {

  public static final String STOREDMAP = "storedmap";
  public static final String STOREDLIST = "storedlist";
  public static final String STOREDREF = "storedRef";
  public static final String STOREDSEQ = "storedSeq";

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private StorageSequenceApi sequenceApi;

  /**
   * This map contains the already used {@link Storage} instances mapped by the schema name.
   */
  private Map<String, Storage> storagesBySchema = new HashMap<>();

  @Autowired(required = false)
  private List<SearchIndex<?>> searchIndices;

  private Map<String, SearchIndex<?>> searchIndexByName = new HashMap<>();

  public CollectionApiStorageImpl() {
    super();
  }

  @Override
  public StoredMap map(String logicalSchema, String mapName) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredMapStorageImpl(getStorage(schema),
        constructGlobalUri(schema, mapName, STOREDMAP), mapName);
  }

  @Override
  public StoredMap map(URI scopeObjectUri, String logicalSchema, String mapName) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredMapStorageImpl(getStorage(schema), constructScopedUri(schema,
        mapName, ObjectStorageImpl.getUriWithoutVersion(scopeObjectUri), STOREDMAP), mapName);
  }

  @Override
  public StoredList list(String logicalSchema, String name) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredListStorageImpl(getStorage(schema),
        constructGlobalUri(schema, name, STOREDLIST), name);
  }

  @Override
  public StoredList list(URI scopeObjectUri, String logicalSchema, String name) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredListStorageImpl(getStorage(schema), constructScopedUri(schema,
        name, ObjectStorageImpl.getUriWithoutVersion(scopeObjectUri), STOREDLIST), name);
  }

  @Override
  public <T> StoredReference<T> reference(String logicalSchema, String name, Class<T> clazz) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredReferenceStorageImpl<>(getStorage(schema),
        constructGlobalUri(schema, name, STOREDREF), name, objectApi.definition(clazz));
  }

  @Override
  public <T> StoredReference<T> reference(URI scopeObjectUri, String logicalSchema, String name,
      Class<T> clazz) {
    String schema = constructCollectionShemaName(logicalSchema);
    return new StoredReferenceStorageImpl<>(getStorage(schema),
        constructScopedUri(schema,
            name, ObjectStorageImpl.getUriWithoutVersion(scopeObjectUri), STOREDREF),
        name, objectApi.definition(clazz));
  }

  @Override
  public <T> StoredReference<T> reference(URI refUri, Class<T> clazz) {
    Storage storage = storageApi.getStorage(refUri);
    return new StoredReferenceStorageImpl<>(storage,
        refUri,
        null, objectApi.definition(clazz));
  }

  @Override
  public <O> SearchIndex<O> searchIndex(String logicalSchema, String name, Class<O> indexedObject) {
    @SuppressWarnings("unchecked")
    SearchIndex<O> result =
        (SearchIndex<O>) searchIndexByName.get(getQualifiedNameOfSearchIndex(logicalSchema, name));
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
    return new StoredSequenceStorageImpl(getStorage(schema),
        constructGlobalUri(schema, name, STOREDSEQ), name, sequenceApi);
  }

  /**
   * This function produce the given logical schema. This schema is currently non versioned by
   * default.
   * 
   * @param logicalSchema
   * @return
   */
  private final synchronized Storage getStorage(String logicalSchema) {
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
    return logicalShema + StringConstant.MINUS_SIGN + "collections";
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (searchIndices != null) {
      searchIndexByName = searchIndices.stream()
          .collect(toMap(i -> getQualifiedNameOfSearchIndex(i.logicalSchema(), i.name()), i -> i));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public SearchIndex<Object> searchIndexComputeIfAbsent(String logicalSchema, String name,
      Supplier<SearchIndex<Object>> searchIndexSupplier) {
    return (SearchIndex<Object>) searchIndexByName.computeIfAbsent(
        getQualifiedNameOfSearchIndex(logicalSchema, name),
        s -> (SearchIndex<Object>) searchIndexSupplier.get());
  }

  private final String getQualifiedNameOfSearchIndex(String logicalSchema, String name) {
    return logicalSchema + StringConstant.DOT + name;
  }

}
