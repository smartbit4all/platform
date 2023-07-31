package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;

abstract class AbstractStoredContainerStorageImpl {

  protected URI uri;

  protected String name;

  protected URI branchUri;

  protected String storageSchema;

  protected BranchApi branchApi;

  protected ObjectApi objectApi;

  protected AbstractStoredContainerStorageImpl(String storageSchema, URI uri, String name) {
    super();
    this.uri = uri;
    this.name = name;
    this.storageSchema = storageSchema;
  }

  public final URI getUri() {
    return uri;
  }

  protected void modifyOnBranch(Consumer<ObjectNode> modification) {
    URI uriToUse = getOrCreate();
    Lock lock = objectApi.getLock(uriToUse);
    lock.lock();
    try {
      ObjectNode objectNode = objectApi.loadLatest(uriToUse);
      modification.accept(objectNode);
      objectApi.save(objectNode);
    } finally {
      lock.unlock();
    }
  }

  protected abstract ObjectNode constructNew(URI uri);

  protected URI getOrCreate() {
    if (branchUri == null) {
      if (!objectApi.exists(uri)) {
        objectApi.save(constructNew(uri));
      }
      return uri;
    }
    BranchEntry branchEntry = objectApi.loadLatest(branchUri).getObject(BranchEntry.class);
    BranchedObject branchedObject = branchEntry.getBranchedObjects().get(uri.toString());
    if (branchedObject == null) {
      ObjectNode storedItemNode = objectApi.loadLatest(uri);
      return objectApi.save(storedItemNode, branchUri);
    } else {
      return branchedObject.getBranchedObjectLatestUri();
    }
  }

  public void branch(URI branchUri) {
    this.branchUri = branchUri;
  }

}
