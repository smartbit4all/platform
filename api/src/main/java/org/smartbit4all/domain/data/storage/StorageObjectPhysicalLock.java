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

  private final boolean inMemory;

  public StorageObjectPhysicalLock(URI objectUri) {
    this(objectUri, false);
  }

  public StorageObjectPhysicalLock(URI objectUri, boolean inMemory) {
    super();
    this.objectUri = objectUri;
    this.inMemory = inMemory;
  }

  public final URI getObjectUri() {
    return objectUri;
  }

  public boolean isInMemory() {
    return inMemory;
  }

}
