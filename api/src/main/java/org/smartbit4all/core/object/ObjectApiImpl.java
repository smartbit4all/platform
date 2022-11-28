package org.smartbit4all.core.object;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.ApplyChangeApi;
import org.smartbit4all.api.object.ObjectRetrievalRequest;
import org.smartbit4all.api.object.RetrievalApi;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;
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
  public ObjectNode load(URI objectUri) {
    ObjectRetrievalRequest request =
        new ObjectRetrievalRequest(this, objectDefinitionApi.definition(objectUri));
    return load(request, objectUri);
  }

  @Override
  public ObjectNode load(ObjectRetrievalRequest request, URI objectUri) {
    return node(retrievalApi.load(request, objectUri));
  }

  @Override
  public List<ObjectNode> load(ObjectRetrievalRequest request, List<URI> objectUris) {
    return retrievalApi.load(request, objectUris).stream()
        .map(this::node)
        .collect(toList());
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
  public <T> ObjectRetrievalRequest request(Class<T> clazz) {
    return new ObjectRetrievalRequest(this, objectDefinitionApi.definition(clazz));
  }

  @Override
  public URI save(ObjectNode node, URI branchUri) {
    return applyChangeApi.applyChanges(node, branchUri);
  }

}
