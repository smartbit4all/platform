package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.UriUtils;

abstract class AbstractStoredContainerStorageImpl {

  protected URI uri;

  protected URI branchUri;

  protected BranchApi branchApi;

  protected ObjectApi objectApi;

  protected StoredCollectionDescriptor descriptor;

  protected AbstractStoredContainerStorageImpl(StoredCollectionDescriptor descriptor, URI uri) {
    super();
    this.uri = uri;
    this.descriptor = descriptor;
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
      // TODO If the given object is new by the branch entry then skip adding as modified.
      if (!objectApi.exists(uri)) {
        objectApi.save(constructNew(uri));
      }
      ObjectNode storedItemNode = objectApi.loadLatest(uri);
      // Construct a special uri for the collection.
      URI branchedUri =
          UriUtils.createUri(uri.getScheme(), null, branchUri.getPath() + uri.getPath(), null);
      storedItemNode.setValue(branchedUri, "uri");
      branchApi.registerCollection(branchUri, getUri(), branchedUri, descriptor);
      return objectApi.save(storedItemNode);
    } else {
      return branchedObject.getBranchedObjectLatestUri();
    }
  }

  public void branch(URI branchUri) {
    if (branchUri != null) {
      this.branchUri = objectApi.getLatestUri(branchUri);
    } else {
      this.branchUri = branchUri;
    }

  }

  public boolean exists() {
    return objectApi.exists(getUri(), branchUri);
  }

  public Long getLastModified() {
    return objectApi.getLastModified(uri);
  }

}
