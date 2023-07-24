package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.BranchOperation.OperationTypeEnum;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.FinalReference;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

  public BranchReferenceApi referenceOf(StoredCollectionDescriptor collection) {

  }

  public BranchReferenceApi referenceOf(URI objectUri, String... path) {

  }

  public BranchReferenceApi listOf(StoredCollectionDescriptor collection) {

  }

  public BranchListApi listOf(URI objectUri, String... path) {

  }

  public BranchReferenceApi mapOf(StoredCollectionDescriptor collection) {

  }

  public BranchReferenceApi mapOf(URI objectUri, String... path) {

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
          if (deleteAlso) {
            b.putDeletedObjectsItem(bo.getSourceObjectLatestUri().toString(),
                bo.getSourceObjectLatestUri());
          }
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


  @Override
  public boolean deleteObject(URI branchUri, URI objectUri) {
    if (branchUri == null || objectUri == null) {
      return false;
    }
    URI latestUri = objectApi.getLatestUri(objectUri);
    BranchedObjectEntry removeBranchedObject =
        removeBranchedObject(branchUri, latestUri, true);
    if (removeBranchedObject == null) {
      // The given uri was not found on the branch so we must add it to the deleted objects.
      ObjectNode branchNode = objectApi.loadLatest(branchUri);
      branchNode.modify(BranchEntry.class, b -> {
        b.putDeletedObjectsItem(latestUri.toString(), latestUri);
        return b;
      });
      objectApi.save(branchNode);
      return true;
    }
    return false;
  }

  private final class BranchedObjectProcessingEntry {

    private BranchedObject branchedObject;

    private boolean processed = false;

    BranchedObjectProcessingEntry(BranchedObject branchedObject) {
      super();
      this.branchedObject = branchedObject;
    }

  }

  @Override
  public List<ObjectNode> merge(URI branchUri) {
    BranchEntry branchEntry = objectApi.read(objectApi.getLatestUri(branchUri), BranchEntry.class);
    // We process the modifications and all the new / deleted object will be reached from these
    // modified objects. The modifications can be collection api collections like StoredList and
    // StoredMap.
    Map<String, BranchedObjectProcessingEntry> modifiedObjects =
        branchEntry.getBranchedObjects().entrySet().stream()
            .collect(toMap(Entry::getKey, e -> new BranchedObjectProcessingEntry(e.getValue())));
    Map<String, BranchedObjectProcessingEntry> newObjects =
        branchEntry.getNewObjects().entrySet().stream()
            .collect(toMap(Entry::getKey, e -> new BranchedObjectProcessingEntry(e.getValue())));

    List<ObjectNode> result = new ArrayList<>();

    // TODO The modification should be ordered?

    return result;
  }

}
