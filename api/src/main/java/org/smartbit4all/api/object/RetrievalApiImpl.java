package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.BranchOperation.OperationTypeEnum;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.storage.bean.ObjectVersion;
import org.smartbit4all.api.value.ValueUris;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toMap;

/**
 * The abstract implementation of the retrieval. It will use contribution apis to access objects.
 *
 * @author Peter Boros
 */
public final class RetrievalApiImpl implements RetrievalApi {

  @Autowired
  private StorageApi storageApi;

  private final Stream<ObjectNodeData> load(RetrievalRequest request, Stream<URI> uriStream,
      BranchEntry branchEntry) {

    return uriStream.parallel()
        .map(u -> readData(request, u, null, null, branchEntry));

  }

  private final ObjectNodeData readData(RetrievalRequest objRequest, Object value,
      String valueScheme, URI valueSetUri, BranchEntry branchEntry) {
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
      data = readDataByUri(objRequest, uri, branchEntry);
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
              readData(refRequest, sourceValue, scheme, ref.getTargetValueSet(), branchEntry));
        } else if (refKind == ReferencePropertyKind.LIST) {
          data.putReferenceListsItem(
              referenceName,
              ((List<?>) sourceValue).stream()
                  .map(v -> readData(refRequest, v, scheme, ref.getTargetValueSet(), branchEntry))
                  .collect(Collectors.toList()));
        } else if (refKind == ReferencePropertyKind.MAP) {
          data.putReferenceMapsItem(
              referenceName,
              ((Map<String, ?>) sourceValue).entrySet().stream()
                  .collect(toMap(
                      Entry::getKey,
                      e -> readData(refRequest, e.getValue(), scheme, ref.getTargetValueSet(),
                          branchEntry))));
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

  private ObjectNodeData readDataByUri(RetrievalRequest objRequest, URI uri,
      BranchEntry branchEntry) {

    URI readUri = getUriToRead(uri, objRequest.isLoadLatest(), branchEntry);

    StorageObject<?> storageObject = storageApi.load(readUri);
    ObjectVersion version = storageObject.getVersion();
    return new ObjectNodeData()
        .objectUri(storageObject.getVersionUri())
        .qualifiedName(storageObject.definition().getQualifiedName())
        .storageSchema(storageObject.getStorage().getScheme())
        .objectAsMap(storageObject.getObjectAsMap())
        .versionNr(version == null ? null : version.getSerialNoData())
        .lastModified(storageObject.getLastModified());
  }

  private final URI getUriToRead(URI uri, boolean loadLatest, BranchEntry branchEntry) {
    URI readUri;

    if (branchEntry != null) {
      // We identify the uri to read if we are reading on the branch.
      Long uriVersion = ObjectStorageImpl.getUriVersion(uri);
      if (loadLatest || uriVersion == null) {
        readUri = ObjectStorageImpl.getUriWithoutVersion(uri);
        BranchedObject branchedObject = getBranchedObject(branchEntry, readUri);
        // We have a branched object for the given object on the branch so we use that instead of
        // the main.
        if (branchedObject != null) {
          readUri = branchedObject.getBranchedObjectLatestUri();
        }
      } else {
        // In this case we must check the version also.
        URI latestUri = ObjectStorageImpl.getUriWithoutVersion(uri);
        BranchedObject branchedObject = getBranchedObject(branchEntry, latestUri);
        // We have a branched object for the given object on the branch so we use that instead of
        // the main.
        if (branchedObject != null) {
          BranchOperation lastRebase = null;
          for (int i = branchedObject.getOperations().size() - 1; i >= 0; i--) {
            BranchOperation bo = branchedObject.getOperations().get(i);
            if (OperationTypeEnum.INIT.equals(bo.getOperationType())
                || OperationTypeEnum.REBASE.equals(bo.getOperationType())) {
              lastRebase = bo;
              break;
            }
          }
          if (lastRebase != null
              && ObjectStorageImpl.getUriVersion(lastRebase.getSourceUri()) != null) {
            Long lastRebaseSourceVersion =
                ObjectStorageImpl.getUriVersion(lastRebase.getSourceUri());
            if (uriVersion < lastRebaseSourceVersion) {
              // We ask for an earlier version from the source. We can read it and return.
              readUri = uri;
            } else if (uriVersion.equals(lastRebaseSourceVersion)) {
              // We exactly ask for the rebased version. On this branch we pass the first version
              // from the branch
              readUri = lastRebase.getTargetUri();
            } else {
              throw new IllegalStateException("Unabe to retrieve a version from the source " + uri
                  + " that is later then the last branching " + lastRebaseSourceVersion);
            }
          } else {
            throw new IllegalStateException("Missing rebase operation for " + branchedObject);
          }
        } else {
          readUri = uri;
        }
      }
    } else {
      readUri = loadLatest ? ObjectStorageImpl.getUriWithoutVersion(uri) : uri;
    }
    return readUri;
  }

  private final BranchedObject getBranchedObject(BranchEntry branchEntry, URI readUri) {
    return branchEntry.getBranchedObjects().get(readUri.toString());
  }

  @Override
  public ObjectNodeData load(RetrievalRequest request, URI uri, BranchEntry branchEntry) {
    return load(request, Stream.of(uri), branchEntry).findFirst().orElse(null);
  }

  @Override
  public List<ObjectNodeData> load(RetrievalRequest request, List<URI> uris,
      BranchEntry branchEntry) {
    return load(request, uris.stream(), branchEntry).collect(Collectors.toList());
  }

  @Override
  public Lock getLock(URI uri) {
    Storage storage = storageApi.getStorage(uri);
    return storage == null ? null : storage.getLock(uri);
  }

  @Override
  public Long getLastModified(URI uri) {
    return storageApi.getDefaultObjectStorage().lastModified(uri);
  }

  @Override
  public boolean exists(URI uri, BranchEntry branchEntry) {
    URI uriToRead = getUriToRead(uri, true, branchEntry);
    return storageApi.getDefaultObjectStorage().exists(uriToRead);
  }

}
