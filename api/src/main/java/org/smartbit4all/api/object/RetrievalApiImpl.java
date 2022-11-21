package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The abstract implementation of the retrieval. It will use contribution apis to access objects.
 * 
 * @author Peter Boros
 */
public final class RetrievalApiImpl implements RetrievalApi {

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private ObjectApi objectApi;

  private final Stream<ObjectNode> load(ObjectRetrievalRequest request, Stream<URI> uriStream) {

    return uriStream.parallel()
        .map(u -> readData(request, u))
        .map(objectApi::node);

  }

  private final ObjectNodeData readData(ObjectRetrievalRequest objRequest, URI uri) {
    // TODO Load latest version from the object.
    // if(objRequest.isLoadLatestVersion()) {
    // }
    StorageObject<?> storageObject = storageApi.load(uri);
    ObjectNodeData data = new ObjectNodeData()
        .objectUri(uri)
        .qualifiedName(storageObject.definition().getQualifiedName()) // TODO Alias?
        .storageSchema(storageObject.getStorage().getScheme())
        .objectAsMap(storageObject.getObjectAsMap())
        .versionNr(storageObject.getVersion().getSerialNoData());

    // Recursive read of all referred objects.
    for (Entry<ReferenceDefinition, ObjectRetrievalRequest> entry : objRequest.getReferences()
        .entrySet()) {
      ReferenceDefinition ref = entry.getKey();

      Object sourceValue = ref.getSourceValue(data.getObjectAsMap());
      if (sourceValue != null) {
        if (sourceValue instanceof URI || sourceValue instanceof String) {
          ObjectNodeData value = readData(entry.getValue(), asUri(sourceValue));
          // TODO refName!!
          data.putReferenceValuesItem(ref.getSourcePropertyPath(), value);
        } else if (sourceValue instanceof List) {
          @SuppressWarnings("unchecked")
          List<ObjectNodeData> readAllRef = ((List<Object>) sourceValue).stream()
              .map(this::asUri)
              .map(u -> readData(entry.getValue(), u))
              .collect(Collectors.toList());
          // TODO refName!!
          data.putReferenceListValuesItem(ref.getSourcePropertyPath(), readAllRef);
        } else if (sourceValue instanceof Map) {
          @SuppressWarnings("unchecked")
          Map<String, URI> refUriMap = ((Map<String, Object>) sourceValue).entrySet().stream()
              .collect(Collectors.toMap(Entry::getKey, e -> asUri(e.getValue())));
          List<String> keys = new ArrayList<>();
          List<URI> uriList = new ArrayList<>();
          for (Entry<String, URI> refEntry : refUriMap.entrySet()) {
            keys.add(refEntry.getKey());
            uriList.add(refEntry.getValue());
          }
          List<ObjectNodeData> readAllRef = uriList.stream()
              .map(u -> readData(entry.getValue(), u))
              .collect(Collectors.toList());
          Map<String, ObjectNodeData> refObjectMap = new HashMap<>();
          ListIterator<String> iterKeys = keys.listIterator();
          for (ObjectNodeData refObjectNode : readAllRef) {
            refObjectMap.put(iterKeys.next(), refObjectNode);
          }
          // TODO refName!!
          data.putReferenceMapValuesItem(ref.getSourcePropertyPath(), refObjectMap);
        }
      }
    }

    return data;
  }

  private URI asUri(Object o) {
    if (o instanceof URI) {
      return (URI) o;
    }
    if (o instanceof String) {
      return URI.create((String) o);
    }
    // TODO log / throw ?
    return null;
  }

  @Override
  public <T> ObjectRetrievalRequest request(Class<T> clazz) {
    return new ObjectRetrievalRequest(this, objectApi.definition(clazz));
  }

  @Override
  public ObjectNode load(ObjectRetrievalRequest request, URI uri) {
    return load(request, Stream.of(uri)).findFirst().orElse(null);
  }

  @Override
  public List<ObjectNode> load(ObjectRetrievalRequest request, URI... uris) {
    return load(request, Stream.of(uris)).collect(Collectors.toList());
  }

  @Override
  public List<ObjectNode> load(ObjectRetrievalRequest request, List<URI> uris) {
    return load(request, uris.stream()).collect(Collectors.toList());
  }

  @Override
  public ObjectRetrievalRequest request(URI objectUri, String... paths) {
    ObjectDefinition<?> definition = objectApi.definition(objectUri);
    return request(definition, paths);
  }

  private ObjectRetrievalRequest request(ObjectDefinition<?> definition, String... paths) {
    ObjectRetrievalRequest request = new ObjectRetrievalRequest(this, definition);
    if (paths != null && paths.length > 0) {
      String path = paths[0];
      ReferenceDefinition reference = definition.getOutgoingReference(path);
      if (reference == null) {
        throw new IllegalArgumentException(
            "Reference " + path + " not found in " + definition.getAlias() + " objectDefinition.");
      }
      String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
      ObjectRetrievalRequest subRequest = request(reference.getTarget(), subPaths);
      request.getReferences().put(reference, subRequest);
    }
    return request;
  }
}
