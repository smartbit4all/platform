package org.smartbit4all.api.collection;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.api.collection.bean.VectorSearchResultItem;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;

public class VectorCollectionImpl implements VectorCollection {

  private VectorDBApi vectorDBApi;

  private ServiceConnection vectorDBService;

  private EmbeddingApi embeddingApi;

  private ServiceConnection embeddingService;

  private String collectionName;

  private ObjectDefinitionApi objectDefinitionApi;

  public VectorCollectionImpl(VectorDBApi vectorDBApi, ServiceConnection vectorDBService,
      EmbeddingApi embeddingApi, ServiceConnection embeddingService, String collectionName) {
    super();
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
  public void addObject(String id, Object obj) {
    vectorDBApi.addPoint(vectorDBService, collectionName, id, embed(obj));
  }

  @Override
  public boolean deletObject(String id) {
    // TODO implement remove on Vector DB api!!!
    return false;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private final VectorValue embed(Object obj) {
    Objects.requireNonNull(obj, "Unable to use null in vector db.");
    if (obj instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) obj;
      return embeddingApi.embed(embeddingService, map);
    } else if (obj instanceof String) {
      return embeddingApi.embed(embeddingService, (String) obj);
    } else {
      // Try to form a Map from the object we have.
      ObjectDefinition objectDefinition = objectDefinitionApi.definition(obj.getClass());
      return embeddingApi.embed(embeddingService, objectDefinition.toMap(obj));
    }
  }

  @Override
  public List<VectorSearchResultItem> search(Object obj, int limit) {
    return vectorDBApi.search(vectorDBService, collectionName, embed(obj), limit);
  }

}
