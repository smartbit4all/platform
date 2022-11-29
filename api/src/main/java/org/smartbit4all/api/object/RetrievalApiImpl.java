package org.smartbit4all.api.object;

import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
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

  private final Stream<ObjectNodeData> load(RetrievalRequest request, Stream<URI> uriStream) {

    return uriStream.parallel()
        .map(u -> readData(request, u));

  }

  private final ObjectNodeData readData(RetrievalRequest objRequest, URI uri) {
    URI readUri = objRequest.isLoadLatest() ? ObjectStorageImpl.getUriWithoutVersion(uri) : uri;
    StorageObject<?> storageObject = storageApi.load(readUri);
    ObjectNodeData data = new ObjectNodeData()
        .objectUri(uri)
        .qualifiedName(storageObject.definition().getQualifiedName())
        .storageSchema(storageObject.getStorage().getScheme())
        .objectAsMap(storageObject.getObjectAsMap())
        .versionNr(storageObject.getVersion().getSerialNoData());

    // Recursive read of all referred objects.
    for (Entry<ReferenceDefinition, RetrievalRequest> refEntry : objRequest.getReferences()
        .entrySet()) {
      ReferenceDefinition ref = refEntry.getKey();
      Object sourceValue = ref.getSourceValue(data.getObjectAsMap());
      if (sourceValue != null) {
        ReferencePropertyKind refKind = ref.getReferencePropertyKind();
        // TODO refName instead of sourcePropertyPath!!
        String referenceName = ref.getSourcePropertyPath();
        RetrievalRequest refRequest = refEntry.getValue();
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
  public ObjectNodeData load(RetrievalRequest request, URI uri) {
    return load(request, Stream.of(uri)).findFirst().orElse(null);
  }

  @Override
  public List<ObjectNodeData> load(RetrievalRequest request, URI... uris) {
    return load(request, Stream.of(uris)).collect(Collectors.toList());
  }

  @Override
  public List<ObjectNodeData> load(RetrievalRequest request, List<URI> uris) {
    return load(request, uris.stream()).collect(Collectors.toList());
  }

}
