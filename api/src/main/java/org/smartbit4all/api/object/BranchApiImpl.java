package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.BranchOperation.OperationTypeEnum;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
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

  private static final Logger log = LoggerFactory.getLogger(BranchApiImpl.class);

  @Autowired
  private ObjectApi objectApi;

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
              u -> new BranchedObject().branchedObjectLatestUri(u).addOperationsItem(
                  new BranchOperation().operationType(OperationTypeEnum.INIT)))));
      return b;
    });
    objectApi.save(objectNode);
  }

}
