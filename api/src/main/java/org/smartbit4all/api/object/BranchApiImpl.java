package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.BranchOperation.OperationTypeEnum;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectCacheEntry;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.core.utility.FinalReference;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * The implementation of the {@link BranchApi} that stores the branch info in {@link BranchEntry}.
 * The branch api manages a cache to effectively access the informations of a branch. The cache will
 * check the freshness of the loaded branch entry by the last modification time.
 *
 * @author Peter Boros
 */
public class BranchApiImpl implements BranchApi {

  protected static final String SCHEME = "branch";

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Override
  public BranchEntry makeBranch(String caption) {
    BranchEntry branch = constructBranch(caption);
    branch.setUri(objectApi.saveAsNew(SCHEME,
        branch));
    return branch;
  }

  @Override
  public BranchEntry constructBranch(String caption) {
    return new BranchEntry().caption(caption)
        .created(sessionApi != null ? sessionApi.createActivityLog() : null);
  }

  @Override
  public Map<URI, BranchOperation> initBranchedObjects(URI branchUri,
      Map<URI, Supplier<URI>> brachedObjects) {
    if (brachedObjects == null || branchUri == null) {
      return Collections.emptyMap();
    }
    Map<URI, BranchOperation> result = new HashMap<>();
    ObjectNode objectNode = objectApi.loadLatest(branchUri).modify(BranchEntry.class, b -> {
      Map<URI, BranchOperation> map =
          brachedObjects.entrySet().stream().collect(toMap(Entry::getKey, e -> {
            URI sourceUri = e.getKey();
            URI latestSourceUri = objectApi.getLatestUri(sourceUri);
            String latestSourceUriString = latestSourceUri.toString();
            return b.getBranchedObjects().computeIfAbsent(latestSourceUriString,
                u -> {
                  URI targetUri = e.getValue().get();
                  return new BranchedObject()
                      .sourceObjectLatestUri(latestSourceUri)
                      .branchedObjectLatestUri(objectApi.getLatestUri(targetUri))
                      .addOperationsItem(
                          new BranchOperation()
                              .sourceUri(sourceUri).targetUri(targetUri)
                              .operationType(OperationTypeEnum.INIT));
                })
                .getOperations().get(0);
          }));
      result.putAll(map);
      return b;
    });
    objectApi.save(objectNode);
    return result;

  }

  @Override
  public void addNewBranchedObjects(URI branchUri, Collection<URI> newObjectUris) {
    if (newObjectUris == null || branchUri == null) {
      return;
    }
    ObjectNode objectNode = objectApi.loadLatest(branchUri).modify(BranchEntry.class, b -> {
      b.getNewObjects().putAll(newObjectUris.stream()
          .collect(toMap(u -> ObjectStorageImpl.getUriWithoutVersion(u).toString(),
              u -> new BranchedObject()
                  .branchedObjectLatestUri(ObjectStorageImpl.getUriWithoutVersion(u))
                  .addOperationsItem(
                      new BranchOperation().operationType(OperationTypeEnum.INIT)))));
      return b;
    });
    objectApi.save(objectNode);
  }

  @Override
  public BranchedObjectEntry removeBranchedObject(URI branchUri, URI branchedObjectUri) {
    return removeBranchedObject(branchUri, branchedObjectUri, false);
  }

  private final BranchedObjectEntry removeBranchedObject(URI branchUri, URI branchedObjectUri,
      boolean deleteAlso) {
    if (branchedObjectUri == null || branchUri == null) {
      return null;
    }
    FinalReference<BranchedObjectEntry> result = new FinalReference<>(null);
    ObjectNode objectNode = objectApi.loadLatest(branchUri).modify(BranchEntry.class, b -> {
      URI latestUri = objectApi.getLatestUri(branchedObjectUri);
      String latestUriString = latestUri.toString();
      BranchedObject newOne =
          b.getNewObjects().remove(latestUriString);
      if (newOne != null) {
        result.set(new BranchedObjectEntry().branchingState(BranchingStateEnum.NEW)
            .branchUri(newOne.getBranchedObjectLatestUri()));
      } else {
        Optional<BranchedObject> firstBranched = b.getBranchedObjects().values().stream()
            .filter(bo -> bo.getBranchedObjectLatestUri().equals(latestUri)).findFirst();
        firstBranched.ifPresent(bo -> {
          b.getBranchedObjects().remove(bo.getSourceObjectLatestUri().toString());
          result.set(new BranchedObjectEntry().branchingState(BranchingStateEnum.MODIFIED)
              .branchUri(bo.getBranchedObjectLatestUri())
              .originalUri(bo.getSourceObjectLatestUri()));
        });
      }
      return b;
    });
    objectApi.save(objectNode);
    return result.get();
  }


  private final class BranchedObjectProcessingEntry {

    BranchedObject branchedObject;

    ObjectNode objectNode;

    Map<URI, List<ObjectNodeReference>> references;

    URI newUri;

    BranchedObjectProcessingEntry(BranchedObject branchedObject, ObjectNode objectNode) {
      super();
      this.branchedObject = branchedObject;
      this.objectNode = objectNode;
    }

  }

  @Override
  public List<ObjectNode> merge(URI branchUri) {
    Objects.requireNonNull(branchUri);
    ObjectCacheEntry<BranchEntry> cacheEntry = objectApi.getCacheEntry(BranchEntry.class);
    BranchEntry branchEntry = cacheEntry.get(branchUri);

    // We load all the branched objects. We try check if the references are branched objects also.
    // We construct the whole graph in advance we check all the references and contained references
    // of the given branched object and collect all the references identified by the branched object
    // uri.
    Map<URI, BranchedObject> branchedObjectsByBranchLatestUri = branchEntry.getBranchedObjects()
        .values().stream().collect(toMap(BranchedObject::getBranchedObjectLatestUri, bo -> bo));

    Map<URI, BranchedObjectProcessingEntry> processingEntiesByBranchUri = new HashMap<>();
    // We assume that the best practice is to save the object in the same order then it was saved
    // originally.
    List<BranchedObjectProcessingEntry> orderedList = new ArrayList<>();
    // This map contains the branched objects referred by another branched object. The key is the
    // referrer and the key inside the map is the referred uri. Both of them are branched. And the
    // values at the end is a list of relevant ObjectNodeReference. All of them must be set with the
    // uri of the newly saved version uri from the source.
    for (BranchedObject branchedObject : branchEntry.getBranchedObjects().values()) {
      ObjectNode objectNode = objectApi.load(branchedObject.getBranchedObjectLatestUri());
      objectNode.overwriteObject(branchedObject.getSourceObjectLatestUri());
      BranchedObjectProcessingEntry processingEntry =
          new BranchedObjectProcessingEntry(branchedObject, objectNode);
      orderedList.add(processingEntry);
      processingEntry.references =
          discoverAllInlineReference(objectNode)
              .filter(onr -> branchedObjectsByBranchLatestUri
                  .containsKey(objectApi.getLatestUri(onr.getObjectUri())))
              .collect(groupingBy(onr -> objectApi.getLatestUri(onr.getObjectUri())));
      processingEntiesByBranchUri.put(branchedObject.getBranchedObjectLatestUri(), processingEntry);
    }

    for (int i = 0; i < orderedList.size(); i++) {
      BranchedObjectProcessingEntry currentEntry = orderedList.get(i);
      currentEntry.newUri = objectApi.save(currentEntry.objectNode);
      // We examine the rest of the entries if they have reference to the newly saved uri the we set
      // the new uri instead of the branched one.
      for (int j = i + 1; j < orderedList.size(); j++) {
        BranchedObjectProcessingEntry examinedEntry = orderedList.get(j);
        List<ObjectNodeReference> refList = examinedEntry.references
            .remove(currentEntry.branchedObject.getBranchedObjectLatestUri());
        if (refList != null) {
          // Set all the uris to the newly saved source uri.
          refList.forEach(onr -> onr.set(currentEntry.newUri));
        }
      }
    }

    // Post process to set the missing references
    for (int i = 0; i < orderedList.size(); i++) {
      BranchedObjectProcessingEntry currentEntry = orderedList.get(i);
      if (!currentEntry.references.isEmpty()) {
        // This entry still have referenced uris that are not replaced up till now.
        currentEntry.references.entrySet().forEach(e -> {
          BranchedObjectProcessingEntry referredEntry = processingEntiesByBranchUri
              .get(currentEntry.branchedObject.getBranchedObjectLatestUri());
          e.getValue().stream().forEach(onr -> onr.set(referredEntry.newUri));
        });
        // After the second save we definitely replaced the branched references.
        objectApi.save(currentEntry.objectNode);
      }
    }

    return orderedList.stream().map(pe -> pe.objectNode).collect(toList());
  }

  private Stream<ObjectNodeReference> discoverAllInlineReference(ObjectNode on) {
    return on.getDefinition().getOutgoingReferences().values().stream().flatMap(rd -> {
      if (rd.getSourceKind() == ReferencePropertyKind.REFERENCE) {
        if (rd.getAggregation() == AggregationKind.INLINE) {
          return discoverAllInlineReference(on.ref(rd.getSourcePropertyPath()).get());
        }
        return Stream.of(on.ref(rd.getSourcePropertyPath()));
      } else if (rd.getSourceKind() == ReferencePropertyKind.LIST) {
        if (rd.getAggregation() == AggregationKind.INLINE) {
          return on.list(rd.getSourcePropertyPath()).nodeStream()
              .flatMap(n -> discoverAllInlineReference(n));
        }
        return on.list(rd.getSourcePropertyPath()).stream();
      } else if (rd.getSourceKind() == ReferencePropertyKind.MAP) {
        if (rd.getAggregation() == AggregationKind.INLINE) {
          return on.map(rd.getSourcePropertyPath()).values().stream()
              .flatMap(onr -> discoverAllInlineReference(onr.get()));
        }
        return on.map(rd.getSourcePropertyPath()).values().stream();
      }
      return Stream.of();
    });
  }

  @Override
  public List<BranchedObjectEntry> compareListByUri(URI branchUri, URI objectUri, String... path) {
    Objects.requireNonNull(branchUri);
    if (objectUri == null) {
      return Collections.emptyList();
    }
    ObjectCacheEntry<BranchEntry> cacheEntry = objectApi.getCacheEntry(BranchEntry.class);
    BranchEntry branchEntry = cacheEntry.get(branchUri);
    Map<URI, BranchedObject> branchMap = branchEntry.getBranchedObjects().values().stream()
        .collect(toMap(bo -> bo.getBranchedObjectLatestUri(), bo -> bo));
    BranchedObject branchedObject =
        branchEntry.getBranchedObjects().values().stream()
            .filter(bo -> objectApi.equalsIgnoreVersion(objectUri, bo.getBranchedObjectLatestUri()))
            .findFirst().orElse(null);
    if (branchedObject != null) {
      // Load the source and the target object too.
      // TODO Use the last operation to identify the exact source object version.
      ObjectNode sourceNode = objectApi.load(branchedObject.getSourceObjectLatestUri());
      ObjectNode branchedNode = objectApi.load(branchedObject.getBranchedObjectLatestUri());
      ObjectNodeList branchedList = branchedNode.list(path);
      // Implementing a merge sort. If we find a deleted object in the source then insert them
      // immediately.
      List<BranchedObjectEntry> result = new ArrayList<>();
      int branchedIndex = 0;
      Map<URI, ObjectNodeReference> sourceMap = sourceNode.list(path).stream()
          .collect(toMap(onr -> objectApi.getLatestUri(onr.getObjectUri()), onr -> onr));
      while (branchedIndex < branchedList.size()) {
        ObjectNodeReference branchedNodeReference = branchedList.get(branchedIndex);
        BranchedObjectEntry newEntry = null;
        BranchedObject branchedReference =
            branchMap.get(objectApi.getLatestUri(branchedNodeReference.getObjectUri()));
        if (branchedReference != null) {
          // This is a branched object so set the given entry to modified.
          newEntry = new BranchedObjectEntry().branchingState(BranchingStateEnum.MODIFIED)
              .originalUri(branchedReference.getSourceObjectLatestUri())
              .branchUri(branchedReference.getBranchedObjectLatestUri());
        } else {
          // Examine if it is a new object in the list or it is deleted. Or if we have the same
          // reference then it remained the same with nop.
          ObjectNodeReference sourceNodeReference =
              sourceMap.remove(branchedNodeReference.getObjectUri());
          if (sourceNodeReference != null) {
            newEntry = new BranchedObjectEntry().branchingState(BranchingStateEnum.NOP)
                .originalUri(branchedNodeReference.getObjectUri())
                .branchUri(branchedNodeReference.getObjectUri());
          } else {
            newEntry = new BranchedObjectEntry().branchingState(BranchingStateEnum.NEW)
                .branchUri(branchedNodeReference.getObjectUri());
          }
        }
        result.add(newEntry);
        branchedIndex++;
      }
      // Now add all the items from the original list as deleted.
      List<BranchedObjectEntry> deletedList = sourceMap.values().stream()
          .map(onr -> new BranchedObjectEntry().originalUri(onr.getObjectUri())
              .branchingState(BranchingStateEnum.DELETED))
          .collect(toList());

      deletedList.sort((boe1, boe2) -> boe1.getOriginalUri().compareTo(boe2.getOriginalUri()));
      result.addAll(deletedList);
      return result;
    } else {
      // We load the object node by the object uri directly and produce the result with nop.
      ObjectNode objectNode = objectApi.loadLatest(objectUri);
      ObjectNodeList list = objectNode.list(path);
      return list.stream()
          .map(onr -> new BranchedObjectEntry().branchingState(BranchingStateEnum.NOP)
              .originalUri(onr.getObjectUri()))
          .collect(toList());
    }
  }

}
