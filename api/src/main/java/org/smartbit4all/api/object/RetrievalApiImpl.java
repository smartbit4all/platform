package org.smartbit4all.api.object;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.BranchOperation.OperationTypeEnum;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.object.bean.RetrievalMode;
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

/**
 * The abstract implementation of the retrieval. It will use contribution apis to access objects.
 *
 * @author Peter Boros
 */
public final class RetrievalApiImpl implements RetrievalApi {

  private static final Logger log = LoggerFactory.getLogger(RetrievalApiImpl.class);

  @Autowired
  private StorageApi storageApi;

  private List<ObjectNodeData> loadBatch(RetrievalRequest request, List<Object> values,
      String valueScheme, URI valueSetUri, BranchEntry branchEntry) {

    // convert values to URIs
    List<URI> urisToLoad = new ArrayList<>(values.size());
    for (Object value : values) {
      URI uri = null;
      if (valueSetUri != null && value instanceof String
          && !((String) value).startsWith(ValueUris.VALUE_SCHEME_PREFIX)) {
        // Handle code reference values with valueSet
        uri = URI.create(valueSetUri.toString() + StringConstant.HASH + (String) value);
      } else {
        uri = UriUtils.asUri(value);
      }
      if (uri != null) {
        urisToLoad.add(uri);
      }
    }

    if (urisToLoad.isEmpty()) {
      // if we have no URIs to load, all values must be direct values
      return values.stream()
          .map(v -> readDataByValue(request.getDefinition(), v, valueScheme, branchEntry))
          .collect(Collectors.toList());
    }

    // handle branch and version logic
    List<URI> readUris = new ArrayList<>();
    for (URI uri : urisToLoad) {
      readUris.add(getUriToRead(uri, request.isLoadLatest(), branchEntry));
    }

    // batch load all objects from storage
    List<StorageObject<?>> storageObjects = storageApi.loadBatch(readUris);

    // Convert to ObjectNodeData and handle branch references
    List<ObjectNodeData> results = new ArrayList<>(storageObjects.size());
    for (StorageObject<?> storageObject : storageObjects) {
      if (storageObject != null) {
        ObjectNodeData data = createObjectNodeData(storageObject);
        correctReferencesOnBranch(request.getDefinition(), data, branchEntry);
        results.add(data);
      }
    }

    // Load all references for all objects in batch
    readReferencesForBatch(request, results, branchEntry);

    if (urisToLoad.size() != results.size()) {
      log.warn("urisToLoad.size() != results.size()");
    }
    return results;
  }

  private void readReferencesForBatch(RetrievalRequest request, List<ObjectNodeData> dataList,
      BranchEntry branchEntry) {
    // Maps to collect all references by ReferenceDefinition
    Map<ReferenceDefinition, List<URI>> allReferencesToLoad = new HashMap<>();
    Map<ReferenceDefinition, Map<URI, List<ObjectNodeData>>> referenceOwners = new HashMap<>();

    // First pass: collect all non-inline references
    for (ObjectNodeData data : dataList) {
      String scheme = data.getStorageSchema();

      // Process NOT INLINE refs from retrievalRequest
      for (Entry<ReferenceDefinition, RetrievalRequest> refEntry : request.getReferences()
          .entrySet()) {
        ReferenceDefinition ref = refEntry.getKey();
        if (ref.getAggregation() != AggregationKind.INLINE) {
          Object sourceValue = ref.getSourceValue(data.getObjectAsMap());
          if (sourceValue != null) {
            List<URI> uris = collectReferencesToLoad(ref, sourceValue);
            // TODO uris may be a Set?
            allReferencesToLoad.computeIfAbsent(ref, k -> new ArrayList<>())
                .addAll(uris);

            // Track which ObjectNodeData instances need these references
            Map<URI, List<ObjectNodeData>> ownersMap =
                referenceOwners.computeIfAbsent(ref, k -> new HashMap<>());
            for (URI uri : uris) {
              ownersMap
                  .computeIfAbsent(uri, k -> new ArrayList<>())
                  .add(data);
            }
          }
        }
      }

      // Process INLINE refs - these are handled directly
      ObjectDefinition<?> definition = request.getDefinition();
      for (Entry<String, ReferenceDefinition> refEntry : definition.getOutgoingReferences()
          .entrySet()) {
        ReferenceDefinition ref = refEntry.getValue();
        if (ref.getAggregation() == AggregationKind.INLINE) {
          Object sourceValue = ref.getSourceValue(data.getObjectAsMap());
          if (sourceValue != null) {
            handleInlineReference(ref, sourceValue, data, scheme, branchEntry);
          }
        }
      }
    }

    // Batch load all referenced objects for each ReferenceDefinition
    // TODO maybe we can load all uris from all referenceDefinition in one loadBatch
    for (Entry<ReferenceDefinition, List<URI>> entry : allReferencesToLoad.entrySet()) {
      ReferenceDefinition ref = entry.getKey();
      List<URI> uris = entry.getValue();

      // Get the appropriate retrieval request for this reference
      RetrievalRequest refRequest = request.getReferences().get(ref);
      RetrievalRequest effectiveRequest =
          refRequest.getContinueRecursionAt() != null ? refRequest.getContinueRecursionAt()
              : refRequest;
      // Load all referenced objects in one batch
      List<ObjectNodeData> loadedObjects =
          loadBatch(effectiveRequest, new ArrayList<>(uris), null, ref.getTargetValueSet(),
              branchEntry);
      Map<URI, ObjectNodeData> loadedMap = loadedObjects.stream()
          .collect(Collectors.toMap(ObjectNodeData::getObjectUri, o -> o));
      Map<Object, ObjectNodeData> loadedLatestMap = null;
      // Distribute loaded objects back to their owners
      Map<URI, List<ObjectNodeData>> ownersMap = referenceOwners.get(ref);
      for (Entry<URI, List<ObjectNodeData>> ownerEntry : ownersMap.entrySet()) {
        URI uri = ownerEntry.getKey();
        ObjectNodeData loaded = loadedMap.get(uri);
        if (loaded == null) {
          URI latestUri = ObjectStorageImpl.getUriWithoutVersion(uri);
          if (Objects.equals(latestUri, uri)) {
            if (loadedLatestMap == null) {
              loadedLatestMap = createLatestNodeMap(loadedMap);
            }
            loaded = loadedLatestMap.get(uri);
          }
        }
        if (loaded != null) {
          for (ObjectNodeData owner : ownerEntry.getValue()) {
            populateReferenceInObject(ref, owner, uri, loaded);
          }
        }
      }
    }
  }

  private Map<Object, ObjectNodeData> createLatestNodeMap(Map<URI, ObjectNodeData> loadedMap) {
    return loadedMap.entrySet().stream()
        .collect(Collectors.toMap(
            e -> ObjectStorageImpl.getUriWithoutVersion(e.getKey()),
            Entry::getValue));
  }



  private void populateReferenceInObject(ReferenceDefinition ref, ObjectNodeData owner, URI uri,
      ObjectNodeData referenced) {
    String referenceName = ref.getSourcePropertyPath();
    ReferencePropertyKind refKind = ref.getReferencePropertyKind();

    switch (refKind) {
      case REFERENCE:
        owner.putReferencesItem(referenceName, referenced);
        break;
      case LIST:
        // Get the original URI list to maintain order
        List<?> sourceList = (List<?>) ref.getSourceValue(owner.getObjectAsMap());
        List<URI> originalUris = UriUtils.asUriList(sourceList);

        // Create or get the list maintaining original order
        List<ObjectNodeData> list = owner.getReferenceLists().computeIfAbsent(referenceName,
            k -> new ArrayList<>(originalUris.size()));

        // Find the position in original list and set at same index
        int index = originalUris.indexOf(uri);
        if (index >= 0) {
          // Ensure list has enough capacity
          while (list.size() <= index) {
            list.add(null);
          }
          list.set(index, referenced);
        }
        break;
      case MAP:
        Map<String, ?> sourceMap = (Map<String, ?>) ref.getSourceValue(owner.getObjectAsMap());
        String key = findKeyForValue(sourceMap, uri);
        if (key != null) {
          owner.getReferenceMaps().computeIfAbsent(referenceName, k -> new HashMap<>())
              .put(key, referenced);
        }
        break;
    }
  }

  private String findKeyForValue(Map<String, ?> map, URI value) {
    for (Entry<String, ?> entry : map.entrySet()) {
      if (value.equals(UriUtils.asUri(entry.getValue()))) {
        return entry.getKey();
      }
    }
    return null;
  }

  // Helper method to create ObjectNodeData from StorageObject
  private ObjectNodeData createObjectNodeData(StorageObject<?> storageObject) {
    ObjectVersion version = storageObject.getVersion();
    return new ObjectNodeData()
        .objectUri(storageObject.getVersionUri())
        .qualifiedName(storageObject.definition().getQualifiedName())
        .storageSchema(storageObject.getStorage().getScheme())
        .objectAsMap(storageObject.getObjectAsMap())
        .aspects(storageObject.getAspects())
        .versionNr(version == null ? null : version.getSerialNoData())
        .lastModified(storageObject.getLastModified())
        .createdAt(version == null ? null : version.getCreatedAt());
  }

  private void handleInlineReference(ReferenceDefinition ref, Object sourceValue,
      ObjectNodeData data, String scheme, BranchEntry branchEntry) {

    String referenceName = ref.getSourcePropertyPath();
    ReferencePropertyKind refKind = ref.getReferencePropertyKind();

    if (refKind == ReferencePropertyKind.REFERENCE
        && !data.getReferences().containsKey(referenceName)) {
      data.putReferencesItem(
          referenceName,
          readDataByValue(ref.getTarget(), sourceValue, scheme, branchEntry));
    } else if (refKind == ReferencePropertyKind.LIST
        && !data.getReferenceLists().containsKey(referenceName)) {
      data.putReferenceListsItem(
          referenceName,
          ((List<?>) sourceValue).stream()
              .map(v -> readDataByValue(ref.getTarget(), v, scheme, branchEntry))
              .collect(Collectors.toList()));
    } else if (refKind == ReferencePropertyKind.MAP
        && !data.getReferenceMaps().containsKey(referenceName)) {
      data.putReferenceMapsItem(
          referenceName,
          ((Map<String, ?>) sourceValue).entrySet().stream()
              .collect(toMap(
                  Entry::getKey,
                  e -> readDataByValue(ref.getTarget(), e.getValue(), scheme, branchEntry))));
    }
  }

  private List<URI> collectReferencesToLoad(ReferenceDefinition ref, Object sourceValue) {
    List<URI> uris = new ArrayList<>();
    ReferencePropertyKind refKind = ref.getReferencePropertyKind();

    if (refKind == ReferencePropertyKind.REFERENCE) {
      URI uri = UriUtils.asUri(sourceValue);
      if (uri != null) {
        uris.add(uri);
      }
    } else if (refKind == ReferencePropertyKind.LIST) {
      uris.addAll(UriUtils.asUriList((List<?>) sourceValue));
    } else if (refKind == ReferencePropertyKind.MAP) {
      uris.addAll(UriUtils.asUriMap((Map<String, ?>) sourceValue).values());
    }
    return uris;
  }

  private void correctReferencesOnBranch(ObjectDefinition<?> objectDefinition, ObjectNodeData data,
      BranchEntry branchEntry) {
    if (branchEntry != null) {
      for (Entry<String, ReferenceDefinition> refEntry : objectDefinition
          .getOutgoingReferences().entrySet()) {
        ReferenceDefinition ref = refEntry.getValue();
        if (ref.getAggregation() != AggregationKind.INLINE) {
          // inline will be read and handled separately
          Object sourceValue = ref.getSourceValue(data.getObjectAsMap());
          if (sourceValue != null) {
            ReferencePropertyKind refKind = ref.getReferencePropertyKind();
            boolean loadLatest = RetrievalRequest.calcLoadLatest(ref, RetrievalMode.NORMAL);
            if (refKind == ReferencePropertyKind.REFERENCE) {
              URI uri = UriUtils.asUri(sourceValue);
              URI uriFromBranch = getUriFromBranchIfExists(uri, loadLatest, branchEntry);
              if (!Objects.equals(uriFromBranch, uri)) {
                sourceValue = uriFromBranch;
                ref.setSourceValue(data.getObjectAsMap(), sourceValue);
              }
            } else if (refKind == ReferencePropertyKind.LIST) {
              List<URI> uris = UriUtils.asUriList((List<?>) sourceValue);
              List<URI> urisFromBranch = uris.stream()
                  .map(uri -> getUriFromBranchIfExists(uri, loadLatest, branchEntry))
                  .collect(toList());
              if (!Objects.equals(urisFromBranch, uris)) {
                sourceValue = urisFromBranch;
                ref.setSourceValue(data.getObjectAsMap(), sourceValue);
              }
            } else if (refKind == ReferencePropertyKind.MAP) {
              @SuppressWarnings("unchecked")
              Map<String, URI> uris = UriUtils.asUriMap((Map<String, ?>) sourceValue);
              Map<String, URI> urisFromBranch = uris.entrySet().stream()
                  .collect(toMap(
                      Entry::getKey,
                      entry -> getUriFromBranchIfExists(entry.getValue(), loadLatest,
                          branchEntry)));
              if (!Objects.equals(urisFromBranch, uris)) {
                sourceValue = urisFromBranch;
                ref.setSourceValue(data.getObjectAsMap(), sourceValue);
              }
            }
          }
        }
      }
    }
  }

  private URI getUriFromBranchIfExists(URI uri, boolean loadLatest, BranchEntry branchEntry) {
    URI uriFromBranch = getUriToRead(uri, loadLatest, branchEntry);
    if (Objects.equals(
        ObjectStorageImpl.getUriWithoutVersion(uri),
        ObjectStorageImpl.getUriWithoutVersion(uriFromBranch))) {
      return uri;
    }
    return uriFromBranch;
  }

  private ObjectNodeData readDataByValue(ObjectDefinition<?> definition, Object value,
      String valueScheme, BranchEntry branchEntry) {
    ObjectNodeData data;
    data = new ObjectNodeData()
        .objectUri(null)
        .qualifiedName(definition.getQualifiedName())
        .storageSchema(valueScheme)
        .objectAsMap((Map<String, Object>) value)
        .versionNr(null);
    // overwrite references based on branch
    correctReferencesOnBranch(definition, data, branchEntry);
    return data;
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
          if (Objects.equals(
              branchedObject.getSourceObjectLatestUri(),
              branchedObject.getBranchedObjectLatestUri())) {
            // this condition indicates its a tagged / snapshotted branchEntry, treat with care..
            // TODO change it so getBranchedObjectLatestUri will be null and handle accordingly
            readUri = getLastRebase(branchedObject).getSourceUri();
          } else {
            readUri = branchedObject.getBranchedObjectLatestUri();
          }
        }
      } else {
        // In this case we must check the version also.
        URI latestUri = ObjectStorageImpl.getUriWithoutVersion(uri);
        BranchedObject branchedObject = getBranchedObject(branchEntry, latestUri);
        // We have a branched object for the given object on the branch so we use that instead of
        // the main.
        if (branchedObject != null) {
          BranchOperation lastRebase = getLastRebase(branchedObject);
          Long lastRebaseSourceVersion =
              ObjectStorageImpl.getUriVersion(lastRebase.getSourceUri());
          if (uriVersion < lastRebaseSourceVersion) {
            // We ask for an earlier version from the source. We can read it and return.
            readUri = uri;
          } else if (uriVersion.equals(lastRebaseSourceVersion)) {
            // We exactly ask for the rebased version. On this branch we pass the first version
            // from the branch
            if (lastRebase.getOperationType() == OperationTypeEnum.TAG) {
              // TAG means we only have sourceUri
              readUri = lastRebase.getSourceUri();
            } else {
              readUri = lastRebase.getTargetUri();
            }
          } else {
            throw new IllegalStateException("Unabe to retrieve a version from the source " + uri
                + " that is later then the last branching " + lastRebaseSourceVersion);
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

  private BranchOperation getLastRebase(BranchedObject branchedObject) {
    BranchOperation lastRebase = null;
    for (int i = branchedObject.getOperations().size() - 1; i >= 0; i--) {
      BranchOperation bo = branchedObject.getOperations().get(i);
      if (OperationTypeEnum.INIT.equals(bo.getOperationType())
          || OperationTypeEnum.REBASE.equals(bo.getOperationType())
          || OperationTypeEnum.TAG.equals(bo.getOperationType())) {
        lastRebase = bo;
        break;
      }
    }
    if (lastRebase == null
        || ObjectStorageImpl.getUriVersion(lastRebase.getSourceUri()) == null) {
      throw new IllegalStateException("Missing rebase operation for " + branchedObject);
    }

    return lastRebase;
  }

  private final BranchedObject getBranchedObject(BranchEntry branchEntry, URI readUri) {
    return branchEntry.getBranchedObjects().get(readUri.toString());
  }

  @Override
  public ObjectNodeData load(RetrievalRequest request, URI uri, BranchEntry branchEntry) {
    if (uri == null) {
      return null;
    }
    List<ObjectNodeData> results = loadBatch(request, Collections.singletonList(uri), branchEntry);
    return results.isEmpty() ? null : results.get(0);
  }

  @Override
  public List<ObjectNodeData> loadBatch(RetrievalRequest request, List<URI> uris,
      BranchEntry branchEntry) {
    if (uris == null || uris.isEmpty()) {
      return Collections.emptyList();
    }
    return loadBatch(request, new ArrayList<>(uris), null, null, branchEntry);
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
