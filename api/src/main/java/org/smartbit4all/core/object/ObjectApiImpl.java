package org.smartbit4all.core.object;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.ApplyChangeApi;
import org.smartbit4all.api.object.RetrievalApi;
import org.smartbit4all.api.object.RetrievalRequest;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.api.object.bean.RetrievalMode;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class ObjectApiImpl implements ObjectApi {

  private static final Logger log = LoggerFactory.getLogger(ObjectApiImpl.class);

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

  @Autowired
  private RetrievalApi retrievalApi;

  @Autowired
  private ApplyChangeApi applyChangeApi;

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
  public ObjectNode loadLatest(URI objectUri, RetrievalMode retrievalMode) {
    return loadInternal(objectUri, retrievalMode, true);
  }

  @Override
  public ObjectNode load(URI objectUri, RetrievalMode retrievalMode) {
    return loadInternal(objectUri, retrievalMode, false);
  }

  private ObjectNode loadInternal(URI objectUri, RetrievalMode retrievalMode, boolean loadLatest) {
    RetrievalRequest request =
        new RetrievalRequest(
            this,
            objectDefinitionApi.definition(objectUri),
            retrievalMode);
    request.setLoadLatest(loadLatest);
    return load(request, objectUri);
  }

  @Override
  public ObjectNode load(RetrievalRequest request, URI objectUri) {
    return node(retrievalApi.load(request, objectUri));
  }

  @Override
  public List<ObjectNode> load(RetrievalRequest request, List<URI> objectUris) {
    return retrievalApi.load(request, objectUris).stream()
        .map(this::node)
        .collect(toList());
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
    return nodeInternal(storageScheme, object);
  }

  @SuppressWarnings("unchecked")
  public <T> ObjectNode nodeInternal(String storageScheme, T object) {
    ObjectDefinition<T> definition = (ObjectDefinition<T>) definition(object.getClass());
    ObjectNodeData data = new ObjectNodeData()
        .objectUri(definition.getUri(object))
        .qualifiedName(definition.getQualifiedName())
        .storageSchema(storageScheme)
        .objectAsMap(definition.toMap(object))
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
    return applyChangeApi.applyChanges(node, branchUri);
  }

  @Override
  public URI getLatestUri(URI uri) {
    return ObjectStorageImpl.getUriWithoutVersion(uri);
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
    if (value instanceof ObjectNodeReference) {
      ObjectNode objectNode = ((ObjectNodeReference) value).get();
      if (objectNode != null) {
        return objectNode.getObject(clazz);
      }
      return null;
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
}
