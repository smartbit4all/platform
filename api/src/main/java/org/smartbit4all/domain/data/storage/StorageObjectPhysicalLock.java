package org.smartbit4all.domain.data.storage;

import java.net.URI;

/**
 * The physical lock can be extended to have values for the specific storage.
 * 
 * @author Peter Boros
 */
public class StorageObjectPhysicalLock {

  /**
   * The uri of the object the lock is stands for.
   */
  private final URI objectUri;

  public StorageObjectPhysicalLock(URI objectUri) {
    super();
    this.objectUri = objectUri;
  }

  public final URI getObjectUri() {
    return objectUri;
  }

}
