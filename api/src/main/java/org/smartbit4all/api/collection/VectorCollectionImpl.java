package org.smartbit4all.api.collection;

import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
  public void ensureExist() {
    if (!vectorDBApi.collectionExists(vectorDBService, collectionName)) {
      vectorDBApi.createCollection(vectorDBService, collectionName);
    }
  }

  @Override
  public void addObject(List<String> idPath, Object obj) {
    vectorDBApi.addPoint(vectorDBService, collectionName, embed(obj).idPath(idPath));
  }

  @Override
  public boolean deleteObject(String id) {
    // TODO implement remove on Vector DB api!!!
    return false;
  }

  @Override
  public void clear() {
    vectorDBApi.createCollection(vectorDBService, collectionName);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  final VectorValue embed(Object obj) {
    Objects.requireNonNull(obj, "Unable to use null in vector db.");
    if (obj instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) obj;
      return embeddingApi.embed(embeddingService, map);
    } else if (obj instanceof String) {
      return embeddingApi.embed(embeddingService, (String) obj);
    } else {
      // Try to form a Map from the object we have.
      ObjectDefinition objectDefinition = objectApi.definition(obj.getClass());
      return embeddingApi.embed(embeddingService, objectDefinition.toMap(obj));
    }
  }

  @Override
  public List<VectorSearchResultItem> search(Object obj, int limit) {
    return vectorDBApi.search(vectorDBService, collectionName, embed(obj), limit);
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
      List<VectorSearchResultItem> result = search(values, parameter.getLimit());
      return new ObjectLookupResult().numberOfRelevant(result.isEmpty() ? 0 : 1).items(result
          .stream().map(si -> new ObjectLookupResultItem()
              .scoreInPercent(si.getScore()).objectAsMap(si.getValue()))
          .collect(toList()));
    }

  }

}
