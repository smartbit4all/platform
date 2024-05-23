package org.smartbit4all.api.collection;

import static java.util.stream.Collectors.toList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.bean.ObjectLookupParameter;
import org.smartbit4all.api.collection.bean.ObjectLookupResult;
import org.smartbit4all.api.collection.bean.ObjectLookupResultItem;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertySet;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;

public class VectorCollectionImpl implements VectorCollection {

  private VectorDBApi vectorDBApi;

  private ServiceConnection vectorDBService;

  private EmbeddingApi embeddingApi;

  private ServiceConnection embeddingService;

  private String collectionName;

  private ObjectApi objectApi;

  private static final Logger log =
      LoggerFactory.getLogger(VectorCollectionImpl.class);

  private static final Logger log_audit =
      LoggerFactory.getLogger(VECTOR_AUDIT_LOG);

  public VectorCollectionImpl(ObjectApi objectApi, VectorDBApi vectorDBApi,
      ServiceConnection vectorDBService,
      EmbeddingApi embeddingApi, ServiceConnection embeddingService, String collectionName) {
    super();
    this.objectApi = objectApi;
    this.vectorDBApi = vectorDBApi;
    this.vectorDBService = vectorDBService;
    this.embeddingApi = embeddingApi;
    this.embeddingService = embeddingService;
    this.collectionName = collectionName;
  }

  @Override
  public void ensureExist() throws IOException {
    if (!vectorDBApi.collectionExists(vectorDBService, collectionName)) {
      vectorDBApi.createCollection(vectorDBService, collectionName);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void addObject(Object obj, List<String> restictedColumns) throws IOException {
    Map<String, Object> objAsMap = objectApi.asType(Map.class, obj);
    VectorValue vectorValue = embed(objAsMap.entrySet().stream()
        .filter(e -> e.getKey() != null && e.getValue() != null)
        .filter(e -> restictedColumns == null || !restictedColumns.contains(e.getKey()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
    if (vectorValue == null) {
      log.error("The embedding failed on object: {}", obj);
      return;
    }
    vectorValue.setInputObject(objAsMap);
    vectorDBApi.addPoint(vectorDBService, collectionName, vectorValue);
  }

  @Override
  public void deleteObject(String id) throws IOException {
    vectorDBApi.deletePoint(vectorDBService, collectionName, id);
  }

  @Override
  public void clear() throws IOException {
    vectorDBApi.deleteCollection(vectorDBService, collectionName);
    vectorDBApi.createCollection(vectorDBService, collectionName);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  final VectorValue embed(Object obj) {
    Objects.requireNonNull(obj, "Unable to use null in vector db.");
    if (obj instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) obj;
      return embeddingApi.embed(embeddingService.getName(), map);
    } else if (obj instanceof String) {
      return embeddingApi.embed(embeddingService.getName(), (String) obj);
    } else {
      // Try to form a Map from the object we have.
      ObjectDefinition objectDefinition = objectApi.definition(obj.getClass());
      return embeddingApi.embed(embeddingService.getName(), objectDefinition.toMap(obj));
    }
  }

  @Override
  public List<VectorSearchResultItem> search(Object obj, int limit) throws IOException {
    if (log_audit.isInfoEnabled()) {
      log_audit.info(">>>>LOOKUP: {} collection for {}", collectionName, obj);
    }
    List<VectorSearchResultItem> result =
        vectorDBApi.search(vectorDBService, collectionName, embed(obj), limit);
    if (log_audit.isInfoEnabled()) {
      log_audit.info(">>>>LOOKUP RESULT: {}", result);
    }
    return result;
  }

  @Override
  public ObjectLookup lookup(ObjectPropertySet searchProperties,
      ObjectMappingDefinition copyBackMapping) {
    return new ObjectLookupVector(objectApi);
  }

  private final class ObjectLookupVector extends ObjectLookup {

    ObjectLookupVector(ObjectApi objectApi) {
      super(objectApi);
    }

    @Override
    public ObjectLookupResult lookup(Object values,
        ObjectLookupParameter parameter) {
      List<VectorSearchResultItem> result = new ArrayList<>();
      try {
        result = search(values, parameter.getLimit());
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
      return new ObjectLookupResult().numberOfRelevant(result.isEmpty() ? 0 : 1).items(result
          .stream().filter(si -> parameter.getRelevanceLimitPercent() <= si.getScore() * 100)
          .map(si -> new ObjectLookupResultItem()
              .scoreInPercent(si.getScore()).objectAsMap(si.getValue()))
          .collect(toList()));
    }

  }

}
