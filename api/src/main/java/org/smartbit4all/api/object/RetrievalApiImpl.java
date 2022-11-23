package org.smartbit4all.api.object;

import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.core.utility.UriUtils;
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
        .qualifiedName(storageObject.definition().getQualifiedName())
        .storageSchema(storageObject.getStorage().getScheme())
        .objectAsMap(storageObject.getObjectAsMap())
        .versionNr(storageObject.getVersion().getSerialNoData());

    // Recursive read of all referred objects.
    for (Entry<ReferenceDefinition, ObjectRetrievalRequest> refEntry : objRequest.getReferences()
        .entrySet()) {
      ReferenceDefinition ref = refEntry.getKey();
      Object sourceValue = ref.getSourceValue(data.getObjectAsMap());
      if (sourceValue != null) {
        ReferencePropertyKind refKind = ref.getReferencePropertyKind();
        // TODO refName instead of sourcePropertyPath!!
        String referenceName = ref.getSourcePropertyPath();
        ObjectRetrievalRequest refRequest = refEntry.getValue();
        if (refKind == ReferencePropertyKind.REFERENCE) {
          data.putReferencesItem(
              referenceName,
              readData(refRequest, UriUtils.asUri(sourceValue)));
        } else if (refKind == ReferencePropertyKind.LIST) {
          data.putReferenceListsItem(
              referenceName,
              UriUtils.asUriList((List<?>) sourceValue).stream()
                  .map(u -> readData(refRequest, u))
                  .collect(Collectors.toList()));
        } else if (refKind == ReferencePropertyKind.MAP) {
          data.putReferenceMapsItem(
              referenceName,
              UriUtils.asUriMap((Map<?, ?>) sourceValue).entrySet().stream()
                  .collect(toMap(
                      Entry::getKey,
                      e -> readData(refRequest, e.getValue()))));
        }
      }
    }

    return data;
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
