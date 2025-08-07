package org.smartbit4all.core.object;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.ApplyChangeApi;
import org.smartbit4all.api.object.RetrievalApi;
import org.smartbit4all.api.object.RetrievalRequest;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.api.object.bean.RetrievalMode;
import org.smartbit4all.api.object.bean.SnapshotData;
import org.smartbit4all.core.utility.ObjectDefinitionUtils;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObjectLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ObjectApiImpl implements ObjectApi {

  private static final Logger log = LoggerFactory.getLogger(ObjectApiImpl.class);

  @Value("${smartbit4all.objectapi.useReadCache:false}")
  private boolean useReadCache;

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

  @Autowired
  private RetrievalApi retrievalApi;

  @Autowired
  @Lazy
  private ObjectApi self;

  @Autowired
  private ApplyChangeApi applyChangeApi;

  @Autowired
  private StorageApi storageApi;

  /**
   * The already initialized {@link ObjectCacheEntry}s in the application.
   */
  private Cache<Class<?>, ObjectCacheEntry<?>> cacheByClass =
      CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

  // Read cache implementation
  private final ThreadLocal<ReadCache> readCache = ThreadLocal.withInitial(() -> null);

  /**
   * Thread-local read cache storage
   */
  private static class ReadCache {
    private final Map<CacheKey, ObjectNodeData> cache = new HashMap<>();
    private final Map<URI, URI> latestUriMapping = new HashMap<>();
    private final Map<CacheKey, Boolean> existsCache = new HashMap<>();
    private final Map<URI, Long> lastModifiedCache = new HashMap<>();

    // cache enable count, indicating how many times the cache has been enabled
    int enabledCount = 0;

    // Debug statistics
    private final long createdAt = System.currentTimeMillis();
    private int cacheHits = 0;
    private int cacheMisses = 0;
    private int existsHits = 0;
    private int existsMisses = 0;
    private int lastModifiedHits = 0;
    private int lastModifiedMisses = 0;

    private void clear() {
      cache.clear();
      latestUriMapping.clear();
      existsCache.clear();
      lastModifiedCache.clear();
    }

    ObjectNodeData get(CacheKey key) {
      ObjectNodeData data = cache.get(key);
      if (data != null) {
        cacheHits++;
      }
      return data;
    }

    void put(CacheKey key, ObjectNodeData data) {
      cache.put(key, data);
    }

    URI getLatestMapping(URI latestUri) {
      return latestUriMapping.get(latestUri);
    }

    void putLatestMapping(URI latestUri, URI versionedUri) {
      latestUriMapping.put(latestUri, versionedUri);
    }

    Boolean getExists(CacheKey key) {
      Boolean exists = existsCache.get(key);
      if (exists != null) {
        existsHits++;
      } else {
        existsMisses++;
      }
      return exists;
    }

    void putExists(CacheKey key, boolean exists) {
      existsCache.put(key, exists);
    }

    Long getLastModified(URI latestUri) {
      Long lastModified = lastModifiedCache.get(latestUri);
      if (lastModified != null) {
        lastModifiedHits++;
      } else {
        lastModifiedMisses++;
      }
      return lastModified;
    }

    void putLastModified(URI latestUri, Long lastModified) {
      lastModifiedCache.put(latestUri, lastModified);
    }

    ObjectNodeData getLoadedObject(URI latestUri, URI branchUri) {
      // Try to get the object from cache
      URI versionedUri = latestUriMapping.get(latestUri);
      if (versionedUri != null) {
        ObjectNodeData data = cache.get(new CacheKey(versionedUri, branchUri));
        if (data != null) {
          return data;
        }
      }
      // Also check with the latest URI directly
      return cache.get(new CacheKey(latestUri, branchUri));
    }

    String getDebugInfo() {
      long duration = System.currentTimeMillis() - createdAt;
      int totalHits = cacheHits + existsHits + lastModifiedHits;
      int totalMisses = cacheMisses + existsMisses + lastModifiedMisses;
      int totalRequests = totalHits + totalMisses;
      double hitRate = totalRequests > 0 ? (double) totalHits / totalRequests * 100 : 0;

      return String.format(
          "ReadCache stats - Duration: %dms, Total requests: %d, Hit rate: %.1f%%, " +
              "Objects(hits/misses): %d/%d, Exists(hits/misses): %d/%d, " +
              "LastModified(hits/misses): %d/%d, Cached objects: %d",
          duration, totalRequests, hitRate,
          cacheHits, cacheMisses,
          existsHits, existsMisses,
          lastModifiedHits, lastModifiedMisses,
          cache.size());
    }
  }

  /**
   * Cache key for storing ObjectNodeData
   */
  private record CacheKey(URI uri, URI branchUri) {
  }

  @Override
  public void enableReadCache() {
    if (useReadCache) {
      ReadCache cache = readCache.get();
      if (cache != null) {
        cache.enabledCount++;
        log.trace("Read cache already enabled for thread: {} - increasing level to {}",
            Thread.currentThread().getName(), cache.enabledCount);
      } else {
        readCache.set(new ReadCache());
        log.trace("Read cache enabled for thread: {}", Thread.currentThread().getName());
      }
    }
  }

  @Override
  public void disableReadCache() {
    disableReadCacheInternal(false);
  }

  /**
   * Disable read cache for the current thread, with force option.
   * 
   * @param force whether to forcefully disable the read cache, ignoring the enabled count.
   */
  private void disableReadCacheInternal(boolean force) {
    if (useReadCache) {
      ReadCache cache = readCache.get();
      if (cache != null) {
        if (cache.enabledCount > 0 && !force) {
          cache.enabledCount--;
          log.trace("Read cache level decreased for thread: {} - new level: {}",
              Thread.currentThread().getName(), cache.enabledCount);
        } else {
          // Clear the cache and remove the thread-local reference
          cache.clear();
          readCache.remove();
          log.trace("Read cache disabled for thread: {} - {}",
              Thread.currentThread().getName(), cache.getDebugInfo());
        }
      }
    }
  }

  @Override
  public boolean isReadCacheEnabled() {
    return useReadCache && readCache.get() != null;
  }

  /**
   * Clear the read cache for the current thread, with force option.
   */
  private void clearReadCache() {
    if (useReadCache) {
      ReadCache cache = readCache.get();
      if (cache != null) {
        if (log.isDebugEnabled()) {
          log.debug("Read cache is being cleared due to a save operation. Disabling read cache. {}",
              cache.getDebugInfo(), new RuntimeException());
        } else if (log.isWarnEnabled()) {
          log.warn("Read cache is being cleared due to a save operation. Disabling read cache. {}",
              cache.getDebugInfo());
        }
        disableReadCacheInternal(true);
      }
    }
  }

  @Override
  public <T> ObjectDefinition<T> definition(Class<T> clazz) {
    return objectDefinitionApi.definition(clazz);
  }

  @Override
  public ObjectDefinition<?> definition(URI objectUri) {
    return objectDefinitionApi.definition(objectUri);
  }

  @Override
  public ObjectDefinition<?> definition(String className) {
    return objectDefinitionApi.definition(className);
  }

  @Override
  public final ObjectSerializer getDefaultSerializer() {
    return objectDefinitionApi.getDefaultSerializer();
  }

  @Override
  public ObjectNode loadLatest(URI objectUri, URI branchUri) {
    return loadInternal(self, objectUri, branchUri, RetrievalMode.NORMAL, true);
  }

  @Override
  public ObjectNode load(URI objectUri, URI branchUri) {
    return loadInternal(self, objectUri, branchUri, RetrievalMode.NORMAL, false);
  }

  @Override
  public ObjectNode loadLatest(String schema, ObjectDefinition<?> definition, String id,
      URI branchUri) {
    Storage storage = storageApi.get(schema);
    URI uri = storage.constructUriForId(definition, id);
    return loadInternal(self, uri, branchUri, RetrievalMode.NORMAL, true);
  }

  @Override
  public ObjectNode load(String schema, ObjectDefinition<?> definition, String id, URI branchUri) {
    Storage storage = storageApi.get(schema);
    URI uri = storage.constructUriForId(definition, id);
    return loadInternal(self, uri, branchUri, RetrievalMode.NORMAL, false);
  }

  static ObjectNode loadInternal(ObjectApi objectApi, URI objectUri, URI branchUri,
      RetrievalMode retrievalMode,
      boolean loadLatest) {
    RetrievalRequest request =
        new RetrievalRequest(
            objectApi,
            objectApi.definition(objectUri),
            retrievalMode);
    request.setLoadLatest(loadLatest);
    return objectApi.load(request, objectUri, branchUri);
  }

  @Override
  public ObjectNode load(RetrievalRequest request, URI objectUri, URI branchUri) {
    ReadCache cache = readCache.get();
    if (cache == null) {
      // Normal behavior when cache is not enabled
      return node(retrievalApi.load(request, objectUri, getBranchEntry(branchUri)))
          .branchUri(branchUri);
    }

    // Read cache is enabled
    URI effectiveUri = objectUri;
    boolean shouldLoadLatest = request.isLoadLatest() || isLatestUri(objectUri);

    // If we should load latest, check if we have a cached mapping
    if (shouldLoadLatest) {
      URI latestUri = getLatestUri(objectUri);
      URI cachedVersionedUri = cache.getLatestMapping(latestUri);
      if (cachedVersionedUri != null) {
        effectiveUri = cachedVersionedUri;
        log.trace("Using cached versioned URI {} for latest URI {}", cachedVersionedUri, latestUri);
      } else {
        // we don't know yet, which version is the latest, load the latest
        effectiveUri = latestUri;
      }
    }

    CacheKey key = new CacheKey(effectiveUri, branchUri);
    ObjectNodeData cachedData = cache.get(key);

    if (cachedData != null) {
      log.trace("Cache hit for URI: {} with branch: {}", effectiveUri, branchUri);
      return node(cachedData).branchUri(branchUri);
    }

    // Cache miss - load from storage
    log.trace("Cache miss for URI: {} with branch: {}", effectiveUri, branchUri);
    ObjectNodeData data = retrievalApi.load(request, objectUri, getBranchEntry(branchUri));

    if (data != null) {
      // Store in cache with the actual versioned URI
      CacheKey versionedKey = new CacheKey(data.getObjectUri(), branchUri);
      cache.put(versionedKey, data);

      // If we loaded latest, also store the mapping
      if (shouldLoadLatest) {
        URI latestUri = getLatestUri(objectUri);
        cache.putLatestMapping(latestUri, data.getObjectUri());
        log.trace("Cached latest URI mapping: {} -> {}", latestUri, data.getObjectUri());
      }
    }

    return node(data).branchUri(branchUri);
  }

  @Override
  public List<ObjectNode> loadBatch(RetrievalRequest request, List<URI> objectUris, URI branchUri) {
    ReadCache cache = readCache.get();
    if (cache == null) {
      // Normal behavior when cache is not enabled
      return retrievalApi.loadBatch(request, objectUris, getBranchEntry(branchUri)).stream()
          .map(this::node).map(node -> node.branchUri(branchUri))
          .collect(toList());
    }

    // With read cache enabled, check cache for each URI
    List<ObjectNode> results = new ArrayList<>();
    List<URI> uncachedUris = new ArrayList<>();
    Map<URI, List<Integer>> uriToIndex = new HashMap<>();

    for (int i = 0; i < objectUris.size(); i++) {
      URI uri = objectUris.get(i);
      URI effectiveUri = uri;
      boolean shouldLoadLatest = request.isLoadLatest() || isLatestUri(uri);

      // If we should load latest, check if we have a cached mapping
      if (shouldLoadLatest) {
        URI latestUri = getLatestUri(uri);
        URI cachedVersionedUri = cache.getLatestMapping(latestUri);
        if (cachedVersionedUri != null) {
          effectiveUri = cachedVersionedUri;
        } else {
          // we don't know yet, which version is the latest, load the latest
          effectiveUri = latestUri;
        }
      }

      CacheKey key = new CacheKey(effectiveUri, branchUri);
      ObjectNodeData cachedData = cache.get(key);

      if (cachedData != null) {
        results.add(node(cachedData).branchUri(branchUri));
      } else {
        uncachedUris.add(uri);
        uriToIndex
            .computeIfAbsent(uri, k -> new ArrayList<>())
            .add(i);
        results.add(null); // Placeholder
      }
    }

    // Load uncached URIs
    if (!uncachedUris.isEmpty()) {
      List<ObjectNodeData> loadedData =
          retrievalApi.loadBatch(request, uncachedUris, getBranchEntry(branchUri));

      for (int i = 0; i < loadedData.size(); i++) {
        ObjectNodeData data = loadedData.get(i);
        URI originalUri = uncachedUris.get(i);
        List<Integer> indexes = uriToIndex.get(originalUri);

        if (data != null) {
          // Store in cache
          CacheKey versionedKey = new CacheKey(data.getObjectUri(), branchUri);
          cache.put(versionedKey, data);

          // If we loaded latest, also store the mapping
          boolean shouldLoadLatest = request.isLoadLatest() || isLatestUri(originalUri);
          if (shouldLoadLatest) {
            URI latestUri = getLatestUri(originalUri);
            cache.putLatestMapping(latestUri, data.getObjectUri());
          }
          indexes.forEach(index -> {
            if (index < results.size()) {
              results.set(index, node(data).branchUri(branchUri));
            } else {
              log.warn("Index {} out of bounds for results size {}. Data: {}", index,
                  results.size(), data);
            }
          });
        }
      }
    }

    return results.stream().filter(Objects::nonNull).collect(toList());
  }

  @Override
  public List<ObjectNode> loadBatch(List<URI> objectUris, URI branchUri) {
    return loadInternalBatch(this, objectUris, branchUri, RetrievalMode.NORMAL, false);
  }

  @Override
  public List<ObjectNode> loadLatestBatch(List<URI> objectUris, URI branchUri) {
    return loadInternalBatch(this, objectUris, branchUri, RetrievalMode.NORMAL, true);
  }

  static List<ObjectNode> loadInternalBatch(ObjectApi objectApi, List<URI> objectUris,
      URI branchUri,
      RetrievalMode retrievalMode,
      boolean loadLatest) {
    if (ObjectUtils.isEmpty(objectUris)) {
      return new ArrayList<>();
    }
    if (objectUris.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException("load List<URI> cannot handle null uris");
    }
    URI objectUri = objectUris.get(0);
    RetrievalRequest request =
        new RetrievalRequest(
            objectApi,
            objectApi.definition(objectUri),
            retrievalMode);
    request.setLoadLatest(loadLatest);
    return objectApi.loadBatch(request, objectUris, branchUri);
  }

  private final BranchEntry getBranchEntry(URI branchUri) {
    BranchEntry branchEntry = null;
    if (branchUri != null) {
      ObjectCacheEntry<BranchEntry> cacheEntry = getCacheEntry(BranchEntry.class);
      branchEntry = cacheEntry.get(branchUri);
    }
    return branchEntry;
  }

  @Override
  public <T> T read(URI uri, Class<T> clazz) {
    return load(uri).getObject(clazz);
  }

  public ObjectNode node(ObjectNodeData data) {
    return new ObjectNode(this, data);
  }

  @Override
  public <T> ObjectNode create(String storageScheme, T object) {
    @SuppressWarnings("unchecked")
    ObjectDefinition<T> definition = (ObjectDefinition<T>) definition(object.getClass());
    boolean hasUri = definition.getUriGetter() != null;
    ObjectNodeData data = new ObjectNodeData()
        .objectUri(hasUri ? definition.getUri(object) : null)
        .qualifiedName(definition.getQualifiedName())
        .storageSchema(storageScheme)
        .objectAsMap(definition.toMap(object))
        .state(ObjectNodeState.NEW)
        .versionNr(null);

    return new ObjectNode(this, definition, data);
  }

  @Override
  public ObjectNode create(String storageScheme, ObjectDefinition<?> definition,
      Map<String, Object> objectMap) {
    boolean hasUri = definition.getUriGetter() != null;

    ObjectNodeData data = new ObjectNodeData()
        .objectUri(hasUri ? asType(URI.class, objectMap.get(ObjectDefinition.URI_PROPERTY)) : null)
        .qualifiedName(definition.getQualifiedName())
        .storageSchema(storageScheme)
        .objectAsMap(objectMap)
        .state(ObjectNodeState.NEW)
        .versionNr(null);

    return new ObjectNode(this, definition, data);
  }

  @Override
  public <T> RetrievalRequest request(Class<T> clazz, RetrievalMode retrievalMode) {
    return new RetrievalRequest(this, objectDefinitionApi.definition(clazz), retrievalMode);
  }

  @Override
  public URI save(ObjectNode node, URI branchUri) {
    // Clear read cache on any save operation
    clearReadCache();

    // TODO lock the branch if exists
    BranchEntry branchEntry = getBranchEntry(branchUri);
    URI result = applyChangeApi.applyChanges(node, branchEntry);
    if (branchEntry != null) {
      ObjectNode objectNode = loadLatest(branchUri);
      objectNode.modify(BranchEntry.class, be -> branchEntry);
      save(objectNode);
    }
    return result;
  }


  @Override
  public URI getLatestUri(URI uri) {
    if (uri == null) {
      return null;
    }
    return ObjectStorageImpl.getUriWithoutVersion(uri);
  }

  @Override
  public boolean isLatestUri(URI uri) {
    if (uri == null) {
      return false;
    }
    URI latestUri = getLatestUri(uri);
    return uri.equals(latestUri);
  }

  @Override
  public ObjectHistoryIterator objectHistory(URI objectUri) {
    java.util.Objects.requireNonNull(objectUri, "objectUri can not be null!");

    URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(objectUri);

    ObjectNode lastObject = loadLatest(uriWithoutVersion);
    long lastVersion = lastObject.getVersionNr();

    return new ObjectHistoryIterator(self, lastVersion, uriWithoutVersion);
  }

  @Override
  public Iterator<ObjectNode> objectHistoryReverse(URI objectUri, URI branchUri) {
    java.util.Objects.requireNonNull(objectUri, "objectUri can not be null!");

    final ObjectNode node = loadLatest(objectUri, branchUri);
    return objectHistoryReverse(node);
  }

  @Override
  public Iterator<ObjectNode> objectHistoryReverseExact(URI objectUri, URI branchUri) {
    java.util.Objects.requireNonNull(objectUri, "objectUri can not be null!");
    final ObjectNode node = load(objectUri, branchUri);
    return objectHistoryReverse(node);
  }

  @Override
  public Iterator<ObjectNode> objectHistoryReverse(final ObjectNode node) {
    Objects.requireNonNull(node, "node cannot be null!");

    final URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(node.getObjectUri());
    final URI branchUri = node.getBranchUri();
    final long lastVersion = node.getVersionNr();

    return new Iterator<>() {

      private long i = lastVersion + 1;

      @Override
      public ObjectNode next() {
        i--;
        if (i < 0) {
          throw new NoSuchElementException(
              "There is no older object verions");
        }
        URI currentObjectUri = ObjectStorageImpl.getUriWithVersion(uriWithoutVersion, i);
        return load(currentObjectUri, branchUri);
      }

      @Override
      public boolean hasNext() {
        return i > 0;
      }
    };
  }

  @Override
  public boolean equalsIgnoreVersion(URI a, URI b) {
    URI uri1 = getLatestUri(a);
    URI uri2 = getLatestUri(b);
    return Objects.equals(uri1, uri2);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T asType(Class<T> clazz, Object value) {
    if (value == null) {
      return null;
    }
    if (clazz.isInstance(value)) {
      return (T) value;
    }
    if (value instanceof ObjectNode) {
      return ((ObjectNode) value).getObject(clazz);
    }
    if (value instanceof ObjectNodeReference) {
      if (clazz == URI.class) {
        return (T) ((ObjectNodeReference) value).getObjectUri();
      }
      ObjectNode objectNode = ((ObjectNodeReference) value).get();
      if (objectNode != null) {
        return objectNode.getObject(clazz);
      }
      return null;
    }
    if (clazz.isEnum()) {
      if (value instanceof String) {
        T[] enumConstants = clazz.getEnumConstants();
        String stringValue = (String) value;
        return enumConstants == null ? null
            : Stream.of(enumConstants).filter(t -> stringValue.equals(t.toString())).findFirst()
                .orElse(null);
      } else {
        return null;
      }
    }
    if (clazz == URI.class && value instanceof String) {
      if (StringConstant.EMPTY.equals(value)) {
        return null;
      }
      return (T) URI.create((String) value);
    }
    if (clazz == UUID.class && value instanceof String) {
      if (StringConstant.EMPTY.equals(value)) {
        return null;
      }
      return (T) UUID.fromString((String) value);
    }
    if (clazz == OffsetDateTime.class && value instanceof String) {
      if (StringConstant.EMPTY.equals(value)) {
        return null;
      }
      return (T) OffsetDateTime.parse((String) value);
    }
    if (clazz == LocalDate.class && value instanceof String) {
      if (StringConstant.EMPTY.equals(value)) {
        return null;
      }
      final String strValue = (String) value;
      try {
        return (T) LocalDate.parse(strValue);
      } catch (DateTimeParseException e1) {
        try {
          return (T) OffsetDateTime.parse(strValue)
              .atZoneSameInstant(ZoneId.systemDefault())
              .toLocalDate();
        } catch (DateTimeParseException e2) {
          if (strValue.length() > 10) { // LocalDate.parse can only handle 10 chars: yyyy-MM-dd
            return (T) LocalDate.parse(strValue.substring(0, 10));
          }
        }
      }
    }
    if (clazz == LocalDateTime.class && value instanceof String) {
      if (StringConstant.EMPTY.equals(value)) {
        return null;
      }
      try {
        return (T) LocalDateTime.parse((String) value);
      } catch (DateTimeParseException e) {
        return (T) OffsetDateTime.parse((String) value)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime();
      }
    }
    if (value instanceof Map) {
      // Try to retrieve the proper object
      return definition(clazz).fromMap((Map<String, Object>) value);
    }

    if (value instanceof String && !clazz.equals(String.class)) {
      return definition(clazz).readFromString((String) value);
    }
    if (clazz.equals(String.class)) {
      return (T) String.valueOf(value);
    }

    if (clazz.equals(Long.class) && value instanceof Integer) {
      // perform primitive widening, then re-box:
      return (T) ((Long) ((Integer) value).longValue());
    }

    if (clazz.equals(Double.class) && value instanceof Float) {
      // perform primitive widening, then re-box:
      return (T) ((Double) ((Float) value).doubleValue());
    }

    throw new IllegalArgumentException(
        "Unable to convert value (" + value.getClass().getName() + ") to " + clazz.getName());
  }

  @Override
  public String asString(Object o) {
    if (o == null) {
      return null;
    }
    try {
      return definition(o.getClass()).writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(
          "Unable to convert value (" + o + ") to String");
    }

  }

  @Override
  public <T> T fromString(String s, Class<T> clazz) {
    if (s == null) {
      return null;
    }
    return definition(clazz).readFromString(s);
  }

  @Override
  public Map<String, Object> toMapObject(Object o) {
    return objectDefinitionApi.toMapObject(o);
  }

  @Override
  public boolean isValue(Object o) {
    return ObjectDefinitionUtils.isValue(o);
  }

  @Override
  public <E> List<E> asList(Class<E> clazz, List<?> value) {
    if (value == null) {
      return new ArrayList<>();
    }
    return value.stream()
        .map(item -> asType(clazz, item))
        .collect(toList());
  }

  @Override
  public <V> Map<String, V> asMap(Class<V> clazz, Map<String, ?> value) {
    if (value == null) {
      return new HashMap<>();
    }
    Map<String, V> result = new HashMap<>();
    for (Entry<String, ?> e : value.entrySet()) {
      result.put(e.getKey(), asType(clazz, e.getValue()));
    }
    return result;
  }

  @Override
  public ObjectNode loadSnapshot(SnapshotData data) {
    return new ObjectNode(this, data);
  }

  @Override
  public <T> T getValueFromObject(Class<T> clazz, Object object, String... paths) {
    if (object == null) {
      return null;
    }
    Map<String, Object> objectAsMap = getObjectAsMap(object);
    return asType(clazz, getValueFromObjectMap(objectAsMap, paths));
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getObjectAsMap(Object object) {
    Map<String, Object> objectAsMap;
    if (object instanceof Map) {
      objectAsMap = (Map<String, Object>) object;
    } else {
      objectAsMap = definition(object.getClass()).toMap(object);
    }
    return objectAsMap;
  }

  @Override
  public <E> List<E> getListFromObject(Class<E> clazz, Object object, String... paths) {
    if (object == null) {
      return Collections.emptyList();
    }
    Map<String, Object> objectAsMap = getObjectAsMap(object);
    Object list = getValueFromObjectMap(objectAsMap, paths);
    if (!(list instanceof List)) {
      throw new IllegalArgumentException(
          "Object on path is not List<>!" + String.join(",", paths));
    }
    return asList(clazz, (List<?>) list);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V> Map<String, V> getMapFromObject(Class<V> clazz, Object object, String... paths) {
    if (object == null) {
      return Collections.emptyMap();
    }
    Map<String, Object> objectAsMap = getObjectAsMap(object);
    Object map = getValueFromObjectMap(objectAsMap, paths);
    if (!(map instanceof Map)) {
      throw new IllegalArgumentException(
          "Object on path is not Map<>!" + String.join(",", paths));
    }
    return asMap(clazz, (Map<String, ?>) map);
  }

  @Override
  public Object setValueIntoObjectMap(Map<String, Object> map, Object newValue, String... paths) {
    return processValueFromObjectMap(map, Optional.of(newValue), paths);
  }

  @Override
  public Object getValueFromObjectMap(Map<String, Object> map, String... paths) {
    return processValueFromObjectMap(map, Optional.empty(), paths);
  }

  public Object processValueFromObjectMap(Map<String, Object> map, Optional<Object> newValue,
      String... paths) {
    if (map == null) {
      return null;
    }
    if (paths != null && paths.length > 0) {
      String path = paths[0];
      Object value = map.get(path);
      if (paths.length == 1) {
        if (newValue.isPresent()) {
          map.put(path, newValue.get());
        }
        return value;
      }
      if (value == null && newValue.isPresent()) {
        value = new HashMap<>();
        map.put(path, value);
      }
      return continueFromFirstValue(value, newValue, paths);
    }
    return map;
  }

  // paths[0] is value, and paths.length > 1, continue based on value's class
  @SuppressWarnings("unchecked")
  private Object continueFromFirstValue(Object value, Optional<Object> newValue, String... paths) {
    if (value == null) {
      return null;
    }
    String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
    if (value instanceof Map) {
      Map<String, Object> subMap = (Map<String, Object>) value;
      return processValueFromObjectMap(subMap, newValue, subPaths);
    }
    if (value instanceof List) {
      List<Object> subList = (List<Object>) value;
      return getValueFromObjectList(subList, newValue, subPaths);
    }
    // TODO any other object - we may try to convert it to a map with it's classes
    // objectDefinition?
    Map<String, Object> objectAsMap = getObjectAsMap(value);
    return processValueFromObjectMap(objectAsMap, newValue, subPaths);
  }

  private Object getValueFromObjectList(List<Object> list, Optional<Object> newValue,
      String... paths) {
    if (list == null) {
      return null;
    }
    if (paths != null && paths.length > 0) {
      String idxString = paths[0];
      Integer idx;
      try {
        idx = Integer.valueOf(idxString);
      } catch (NumberFormatException ex1) {
        throw new IllegalArgumentException("List index is not a number: "
            + "(" + idxString + ")");
      } catch (IndexOutOfBoundsException ex2) {
        throw new IllegalArgumentException("List item not found by index: "
            + "(" + idxString + ")");
      }
      Object value = list.get(idx);
      if (paths.length == 1) {
        return value;
      }
      return continueFromFirstValue(value, newValue, paths);
    }
    return list;
  }

  @Override
  public ObjectPropertyResolver resolver() {
    return new ObjectPropertyResolver(self);
  }

  @Override
  public ObjectMapping mapper() {
    return new ObjectMapping(self);
  }

  @Override
  public ContextMapping contextMapper() {
    return new ContextMapping(self);
  }

  @Override
  public ContextObject contextObject() {
    return new ContextObject(self);
  }

  @Override
  public Lock getLock(URI uri) {
    return retrievalApi.getLock(uri);
  }

  @Override
  public List<Lock> lockAll(List<URI> uris) {
    Objects.requireNonNull(uris);
    List<Lock> locks = uris.stream()
        .map(this::getLock)
        .collect(toList());
    // Try to retrieve all the locks but release the already retrieved ones if there is any lock
    // that is unavailable. Wait a little bit and try again.
    List<Lock> result = new ArrayList<>();
    boolean firstRun = true;
    while (result.size() != locks.size()) {
      if (!firstRun) {
        // don't sleep on first run
        try {
          Thread.sleep(150);
        } catch (InterruptedException e) {
          throw new IllegalStateException(
              "Unable to retrieve all locks for the following objects (" + uris + ")", e);
        }
      }
      firstRun = false;
      for (Lock lock : locks) {
        if (lock.tryLock()) {
          result.add(lock);
        } else {
          result.stream().forEach(l -> {
            if (l instanceof StorageObjectLock) {
              ((StorageObjectLock) l).unlockIgnoreTransaction();
            } else {
              l.unlock();
            }
          });
          result.clear();
          break;
        }
      }
    }
    return result;
  }

  @Override
  public void unlockAll(List<Lock> locks) {
    Objects.requireNonNull(locks);
    List<Lock> unlocking = new ArrayList<>(locks);
    Collections.reverse(unlocking);
    for (Lock lock : unlocking) {
      lock.unlock();
    }
  }

  @Override
  public Long getLastModified(URI uri) {
    ReadCache cache = readCache.get();
    if (cache == null) {
      // Normal behaviour when cache is not enabled
      return retrievalApi.getLastModified(uri);
    }

    // Always use latest URI for last modified
    URI latestUri = getLatestUri(uri);

    // Check lastModified cache first
    Long cachedLastModified = cache.getLastModified(latestUri);
    if (cachedLastModified != null) {
      log.trace("Cache hit for getLastModified: {}", latestUri);
      return cachedLastModified;
    }

    // Check if we have already loaded this object - if yes, get lastModified from it
    ObjectNodeData loadedData = cache.getLoadedObject(latestUri, null);
    if (loadedData != null && loadedData.getLastModified() != null) {
      log.trace("Getting lastModified from already loaded object: {}", latestUri);
      Long lastModified = loadedData.getLastModified();
      cache.putLastModified(latestUri, lastModified);
      return lastModified;
    }

    // Object not in cache - load the latest version to get lastModified
    // This is an optimization since getLastModified is often followed by a load
    log.trace("Loading object to get lastModified (and cache for future use): {}", latestUri);

    try {
      ObjectNode node = loadLatest(uri);
      if (node != null) {
        Long lastModified = node.getLastModified();
        // The object is now cached through the load operation
        // Also cache the lastModified separately
        if (lastModified != null) {
          cache.putLastModified(latestUri, lastModified);
        }
        return lastModified;
      }
    } catch (Exception e) {
      log.trace("Failed to load object for lastModified, falling back to retrievalApi: {}",
          latestUri, e);
      // Fallback to direct retrievalApi call if load fails
      Long lastModified = retrievalApi.getLastModified(uri);
      if (lastModified != null) {
        cache.putLastModified(latestUri, lastModified);
      }
      return lastModified;
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> ObjectCacheEntry<T> getCacheEntry(Class<T> clazz) {
    try {
      return (ObjectCacheEntry<T>) cacheByClass.get(clazz,
          () -> new ObjectCacheEntryImpl<>(clazz).objectApi(self));
    } catch (ExecutionException e) {
      throw new IllegalArgumentException("Unable to initiate cache for the " + clazz);
    }
  }

  @Override
  public boolean exists(URI uri) {
    return exists(uri, null);
  }

  @Override
  public boolean exists(URI uri, URI branchUri) {
    ReadCache cache = readCache.get();
    if (cache == null) {
      // Normal behaviour when cache is not enabled
      return retrievalApi.exists(uri, getBranchEntry(branchUri));
    }

    // Always use latest URI for exists check
    URI latestUri = getLatestUri(uri);
    CacheKey key = new CacheKey(latestUri, branchUri);

    // Check exists cache first
    Boolean cachedExists = cache.getExists(key);
    if (cachedExists != null) {
      log.trace("Cache hit for exists check: {} with branch: {}", latestUri, branchUri);
      return cachedExists;
    }

    // Check if we have already loaded this object - if yes, it exists
    if (cache.getLoadedObject(latestUri, branchUri) != null) {
      log.trace("Object already loaded in cache, marking as exists: {} with branch: {}", latestUri,
          branchUri);
      cache.putExists(key, true);
      return true;
    }

    // Cache miss - check storage
    log.trace("Cache miss for exists check: {} with branch: {}", latestUri, branchUri);
    boolean exists = retrievalApi.exists(uri, getBranchEntry(branchUri));

    // Cache the result
    cache.putExists(key, exists);

    return exists;
  }

  @Override
  public boolean exists(String schema, ObjectDefinition<?> definition, String id, URI branchUri) {
    Storage storage = storageApi.get(schema);
    URI uri = storage.constructUriForId(definition, id);
    return exists(uri, branchUri);
  }

}
