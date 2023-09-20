package org.smartbit4all.core.object;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
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
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ObjectApiImpl implements ObjectApi {

  private static final Logger log = LoggerFactory.getLogger(ObjectApiImpl.class);

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

  @Autowired
  private RetrievalApi retrievalApi;

  @Autowired
  private ObjectApi self;

  @Autowired
  private ApplyChangeApi applyChangeApi;

  /**
   * The already initialized {@link ObjectCacheEntry}s in the application.
   */
  private Cache<Class, ObjectCacheEntry<?>> cacheByClass =
      CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

  @SuppressWarnings("unchecked")
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
    return loadInternal(objectUri, branchUri, RetrievalMode.NORMAL, true);
  }

  @Override
  public ObjectNode load(URI objectUri, URI branchUri) {
    return loadInternal(objectUri, branchUri, RetrievalMode.NORMAL, false);
  }

  private ObjectNode loadInternal(URI objectUri, URI branchUri, RetrievalMode retrievalMode,
      boolean loadLatest) {
    RetrievalRequest request =
        new RetrievalRequest(
            this,
            objectDefinitionApi.definition(objectUri),
            retrievalMode);
    request.setLoadLatest(loadLatest);
    return load(request, objectUri, branchUri);
  }

  @Override
  public ObjectNode load(RetrievalRequest request, URI objectUri, URI branchUri) {
    return node(retrievalApi.load(request, objectUri, getBranchEntry(branchUri)))
        .branchUri(branchUri);
  }

  @Override
  public List<ObjectNode> load(RetrievalRequest request, List<URI> objectUris, URI branchUri) {
    return retrievalApi.load(request, objectUris, getBranchEntry(branchUri)).stream()
        .map(this::node).map(node -> node.branchUri(branchUri))
        .collect(toList());
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
  public ObjectNode create(String storageScheme, Object object) {
    ObjectDefinition<Object> definition = (ObjectDefinition<Object>) definition(object.getClass());
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
  public Iterator<ObjectNode> objectHistory(URI objectUri, URI branchUri) {
    java.util.Objects.requireNonNull(objectUri, "objectUri can not be null!");

    URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(objectUri);

    ObjectNode lastObject = loadLatest(uriWithoutVersion);
    long lastVersion = lastObject.getVersionNr();

    return new Iterator<ObjectNode>() {

      private long i = -1;

      @Override
      public ObjectNode next() {
        i++;
        if (i > lastVersion) {
          throw new NoSuchElementException(
              "There is no object with version greater than " + lastVersion);
        }
        URI currentObjectUri = ObjectStorageImpl.getUriWithVersion(uriWithoutVersion, i);
        return load(currentObjectUri, branchUri);
      }

      @Override
      public boolean hasNext() {
        return i < lastVersion;
      }
    };
  }

  @Override
  public Iterator<ObjectNode> objectHistoryReverse(URI objectUri, URI branchUri) {
    java.util.Objects.requireNonNull(objectUri, "objectUri can not be null!");

    URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(objectUri);

    ObjectNode lastObject = loadLatest(uriWithoutVersion);
    long lastVersion = lastObject.getVersionNr();

    return new Iterator<ObjectNode>() {

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
    return Objects.equal(uri1, uri2);
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
      return (T) LocalDate.parse((String) value);
    }
    if (clazz == LocalDateTime.class && value instanceof String) {
      if (StringConstant.EMPTY.equals(value)) {
        return null;
      }
      return (T) LocalDateTime.parse((String) value);
    }
    if (value instanceof Map) {
      // Try to retrieve the proper object
      return definition(clazz).fromMap((Map<String, Object>) value);
    }
    if (value instanceof String && !clazz.equals(String.class)) {
      try {
        return getDefaultSerializer().fromString((String) value, clazz);
      } catch (IOException e) {
        throw new IllegalArgumentException(
            "Unable to convert value (" + value.getClass().getName() + ") to" + clazz.getName());
      }
    }
    throw new IllegalArgumentException(
        "Unable to convert value (" + value.getClass().getName() + ") to" + clazz.getName());
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
    return value.entrySet().stream()
        .collect(toMap(
            Entry::getKey,
            e -> asType(clazz, e.getValue())));
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
  public Object getValueFromObjectMap(Map<String, Object> map, String... paths) {
    if (map == null) {
      return null;
    }
    if (paths != null && paths.length > 0) {
      String path = paths[0];
      Object value = map.get(path);
      if (paths.length == 1) {
        return value;
      }
      return continueFromFirstValue(value, paths);
    }
    return map;
  }

  // paths[0] is value, and paths.length > 1, continue based on value's class
  @SuppressWarnings("unchecked")
  private Object continueFromFirstValue(Object value, String... paths) {
    if (value == null) {
      return null;
    }
    String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
    if (value instanceof Map) {
      Map<String, Object> subMap = (Map<String, Object>) value;
      return getValueFromObjectMap(subMap, subPaths);
    }
    if (value instanceof List) {
      List<Object> subList = (List<Object>) value;
      return getValueFromObjectList(subList, subPaths);
    }
    // TODO any other object - we may try to convert it to a map with it's classes
    // objectDefinition?
    Map<String, Object> objectAsMap = getObjectAsMap(value);
    return getValueFromObjectMap(objectAsMap, subPaths);
  }

  private Object getValueFromObjectList(List<Object> list, String... paths) {
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
      return continueFromFirstValue(value, paths);
    }
    return list;
  }

  @Override
  public ObjectPropertyResolver resolver() {
    return new ObjectPropertyResolver(self);
  }

  @Override
  public Lock getLock(URI uri) {
    return retrievalApi.getLock(uri);
  }

  @Override
  public Long getLastModified(URI uri) {
    return retrievalApi.getLastModified(uri);
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
    return retrievalApi.exists(uri, null);
  }

  @Override
  public boolean exists(URI uri, URI branchUri) {
    return retrievalApi.exists(uri, getBranchEntry(branchUri));
  }

}
