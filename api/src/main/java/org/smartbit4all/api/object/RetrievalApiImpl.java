package org.smartbit4all.api.object;

import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.value.ValueUris;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.core.utility.StringConstant;
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

  private final Stream<ObjectNodeData> load(RetrievalRequest request, Stream<URI> uriStream,
      URI branchUri) {

    return uriStream.parallel()
        .map(u -> readData(request, u, null, null, branchUri));

  }

  private final ObjectNodeData readData(RetrievalRequest objRequest, Object value,
      String valueScheme, URI valueSetUri, URI branchUri) {
    URI uri = null;
    if (valueSetUri != null && value instanceof String
        && !((String) value).startsWith(ValueUris.VALUE_SCHEME_PREFIX)) {
      // In this case we know the value set of the given value but we doesn't have a value value URI
      // just a code reference.
      uri = URI.create(valueSetUri.toString() + StringConstant.HASH + (String) value);
    } else {
      uri = UriUtils.asUri(value);
    }
    ObjectNodeData data;
    if (uri != null) {
      data = readDataByUri(objRequest, uri);
    } else {
      data = readDataByValue(objRequest.getDefinition(), value, valueScheme);
    }
    String scheme = data.getStorageSchema();
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
              readData(refRequest, sourceValue, scheme, ref.getTargetValueSet(), branchUri));
        } else if (refKind == ReferencePropertyKind.LIST) {
          data.putReferenceListsItem(
              referenceName,
              ((List<?>) sourceValue).stream()
                  .map(v -> readData(refRequest, v, scheme, ref.getTargetValueSet(), branchUri))
                  .collect(Collectors.toList()));
        } else if (refKind == ReferencePropertyKind.MAP) {
          data.putReferenceMapsItem(
              referenceName,
              ((Map<String, ?>) sourceValue).entrySet().stream()
                  .collect(toMap(
                      Entry::getKey,
                      e -> readData(refRequest, e.getValue(), scheme, ref.getTargetValueSet(),
                          branchUri))));
        }
      }
    }
    // read all inline references, which are not read yet
    // TODO this is not recursive now, we should create a fake RetrievalRequest. That way we can
    // join this for loop with the previous
    for (Entry<String, ReferenceDefinition> refEntry : objRequest.getDefinition()
        .getOutgoingReferences().entrySet()) {
      ReferenceDefinition ref = refEntry.getValue();
      if (ref.getAggregation() != AggregationKind.INLINE) {
        continue;
      }
      Object sourceValue = ref.getSourceValue(data.getObjectAsMap());
      if (sourceValue != null) {
        ReferencePropertyKind refKind = ref.getReferencePropertyKind();
        // TODO refName instead of sourcePropertyPath!!
        String referenceName = ref.getSourcePropertyPath();
        if (refKind == ReferencePropertyKind.REFERENCE
            && !data.getReferences().containsKey(referenceName)) {
          data.putReferencesItem(
              referenceName,
              readDataByValue(ref.getTarget(), sourceValue, scheme));
        } else if (refKind == ReferencePropertyKind.LIST
            && !data.getReferenceLists().containsKey(referenceName)) {
          data.putReferenceListsItem(
              referenceName,
              ((List<?>) sourceValue).stream()
                  .map(v -> readDataByValue(ref.getTarget(), v, scheme))
                  .collect(Collectors.toList()));
        } else if (refKind == ReferencePropertyKind.MAP
            && !data.getReferenceMaps().containsKey(referenceName)) {
          data.putReferenceMapsItem(
              referenceName,
              ((Map<String, ?>) sourceValue).entrySet().stream()
                  .collect(toMap(
                      Entry::getKey,
                      e -> readDataByValue(ref.getTarget(), e.getValue(), scheme))));
        }
      }
    }

    return data;
  }

  private ObjectNodeData readDataByValue(ObjectDefinition<?> definition, Object value,
      String valueScheme) {
    ObjectNodeData data;
    data = new ObjectNodeData()
        .objectUri(null)
        .qualifiedName(definition.getQualifiedName())
        .storageSchema(valueScheme)
        .objectAsMap((Map<String, Object>) value)
        .versionNr(null);
    return data;
  }

  private ObjectNodeData readDataByUri(RetrievalRequest objRequest, URI uri) {
    URI readUri = objRequest.isLoadLatest() ? ObjectStorageImpl.getUriWithoutVersion(uri) : uri;
    StorageObject<?> storageObject = storageApi.load(readUri);
    ObjectVersion version = storageObject.getVersion();
    return new ObjectNodeData()
        .objectUri(storageObject.getVersionUri())
        .qualifiedName(storageObject.definition().getQualifiedName())
        .storageSchema(storageObject.getStorage().getScheme())
        .objectAsMap(storageObject.getObjectAsMap())
        .versionNr(version == null ? null : version.getSerialNoData());
  }

  @Override
  public ObjectNodeData load(RetrievalRequest request, URI uri, URI branchUri) {
    return load(request, Stream.of(uri), branchUri).findFirst().orElse(null);
  }

  @Override
  public List<ObjectNodeData> load(RetrievalRequest request, List<URI> uris, URI branchUri) {
    return load(request, uris.stream(), branchUri).collect(Collectors.toList());
  }

}
