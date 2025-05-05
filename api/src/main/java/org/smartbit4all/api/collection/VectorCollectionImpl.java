package org.smartbit4all.api.collection;

import static java.util.stream.Collectors.toList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.bean.ObjectLookupParameter;
import org.smartbit4all.api.collection.bean.ObjectLookupResult;
import org.smartbit4all.api.collection.bean.ObjectLookupResultItem;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
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
  public Boolean exists() {
    return vectorDBApi.collectionExists(vectorDBService, collectionName);
  }

  @Override
  public void ensureExist() {
    if (!exists()) {
      vectorDBApi.createCollection(vectorDBService, collectionName);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public String addObject(Object obj, List<String> restictedColumns) {
    Map<String, Object> map = objectApi.asType(Map.class, obj);
    return add(map.entrySet().stream()
        .filter(e -> restictedColumns == null || !restictedColumns.contains(e.getKey()))
        .filter(e -> e.getKey() != null && e.getValue() != null)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue)), map);
  }

  @Override
  public String add(Map<String, Object> value, Map<String, Object> additionalData) {
    VectorValue vectorValue = embed(value.entrySet().stream()
        .filter(e -> e.getKey() != null && e.getValue() != null)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
    if (vectorValue == null) {
      throw new IllegalArgumentException("The embedding failed on object: " + value);
    }
    vectorValue.setInputObject(additionalData);
    return vectorDBApi.addPoint(vectorDBService, collectionName, vectorValue);
  }

  @Override
  public String add(String value, Map<String, Object> additionalData) {
    VectorValue vectorValue = embed(value);
    if (vectorValue == null) {
      throw new IllegalArgumentException("The embedding failed on object: " + value);
    }
    vectorValue.setInputObject(additionalData);
    return vectorDBApi.addPoint(vectorDBService, collectionName, vectorValue);
  }

  @Override
  public void delete(String id) {
    vectorDBApi.deletePoint(vectorDBService, collectionName, id);
  }

  @Override
  public void delete(Collection<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return;
    }
    vectorDBApi.deletePoints(vectorDBService, collectionName,
        ids instanceof List ? (List) ids : ids.stream().collect(toList()));
  }

  @Override
  public void clear() {
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
  public List<VectorSearchResultItem> search(Object obj, int limit) {
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
  public ObjectLookup lookup() {
    return new ObjectLookupVector(objectApi);
  }

  private final class ObjectLookupVector extends ObjectLookup {

    ObjectLookupVector(ObjectApi objectApi) {
      super(objectApi);
    }

    @Override
    public ObjectLookupResult lookup(Object values,
        ObjectLookupParameter parameter) {
      List<VectorSearchResultItem> resultList = search(values, parameter.getLimit());

      if (parameter.getRelevanceLimitRange() != null) {
        // We cut the end of the list if needed
        resultList.sort(Comparator.comparing(e -> e.getScore()));
        int index = 0;
        for (int i = 0; i < resultList.size(); i++) {
          if (i > 0 && Math.abs(resultList.get(i - 1).getScore()
              - resultList.get(i).getScore()) > parameter.getRelevanceLimitRange()) {
            index = i;
            break;
          }
        }
        if (index != 0) {
          resultList = IntStream.range(0, index)
              .mapToObj(resultList::get)
              .collect(Collectors.toList());
        }
      }

      Float relevanceLimitPercent =
          parameter.getRelevanceLimitPercent() != null ? parameter.getRelevanceLimitPercent() : 0;

      ObjectLookupResult lookupResult = new ObjectLookupResult().items(resultList
          .stream()
          .filter(si -> relevanceLimitPercent <= si.getScore() * 100)
          .map(si -> new ObjectLookupResultItem()
              .id(si.getId())
              .scoreInPercent(si.getScore()).objectAsMap(si.getValue()))
          .collect(toList()));
      lookupResult.numberOfRelevant(lookupResult.getItems().size());
      return lookupResult;
    }

  }

}
