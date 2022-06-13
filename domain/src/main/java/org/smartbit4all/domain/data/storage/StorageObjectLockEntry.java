package org.smartbit4all.domain.data.storage;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.smartbit4all.core.utility.ListBasedMap;

/**
 * This entry is the common lock entry for the {@link ObjectStorage} implementations.
 * 
 * @author Peter Boros
 */
final class StorageObjectLockEntry {

  private static class InstanceEntry {

    InstanceEntry(StorageObjectLock instance) {
      super();
      this.instance = new WeakReference<>(instance);
    }

    WeakReference<StorageObjectLock> instance;

  }

  /**
   * The URI of the object the lock belongs to.
   */
  private final URI objectURI;

  /**
   * The lock that ensure the mutual exclusion on the registered instances.
   */
  private final Lock mutexInstanceRegister = new ReentrantLock(true);

  /**
   * The id sequence is used to generate unique identifier for the given lock. No need for the
   * {@link AtomicLong} because it's guarded by the {@link #mutexInstanceRegister}.
   */
  private long idSequence = 0;

  /**
   * The mutex to ensure the exclusive execution lock on an object.
   */
  private final ReentrantLock mutexExecution = new ReentrantLock(true);

  /**
   * The instance register contains all the instances with {@link WeakReference}. If we missed
   * unlocking but we loose the reference for the lock then this reference is going to be empty so a
   * cleanup mechanism can detect and unlock this entry.
   */
  private final Map<Long, InstanceEntry> instanceRegister = new ListBasedMap<>();

  /**
   * This is a supplier for the physical lock. Can be used to acquire the physical lock when the
   * first {@link StorageObjectLock} is activated via an operation.
   */
  private Supplier<StorageObjectPhysicalLock> acquirePhysicalLock;

  /**
   * If the given {@link ObjectStorage} implementation supports then this object holds the physical.
   */
  private StorageObjectPhysicalLock physicalLock;

  /**
   * When the last thread leave the given {@link StorageObjectLock} then this releaser is free the
   * physical resource.
   */
  private Consumer<StorageObjectPhysicalLock> releaser;

  /**
   * This call back removes the lock itself from the registry managed by the
   * {@link ObjectStorageImpl}. Used by the destruction process when the last thread leaves the
   * lock.
   */
  private Consumer<URI> lockRemover;

  /**
   * Constructs an object lock owned by the actual thread first. The current thread won't be
   * blocked.
   * 
   * @param objectURI
   * @param acquire This supplier can be injected by the given {@link ObjectStorage} implementation.
   *        When constructing a {@link StorageObjectLock} this function will acquire a physical lock
   *        on the storage to give an exclusive access to the given object and avoid parallel
   *        modification and inconsistency.
   * 
   */
  StorageObjectLockEntry(URI objectURI,
      Supplier<StorageObjectPhysicalLock> acquire,
      Consumer<StorageObjectPhysicalLock> releaser) {
    super();
    this.objectURI = objectURI;
    if (acquire != null) {
      if (releaser == null) {
        throw new IllegalArgumentException(
            "Unable to initate the StorageObjectLock, the the physical lock release method is missing.");
      }
      this.releaser = releaser;
      acquirePhysicalLock = acquire;
    }
  }

  /**
   * Register a new instance to the entry. This doesn't mean lock because the lock can be placed
   * with {@link StorageObjectLock#lock()}.
   * 
   * @return The new lock instance.
   */
  StorageObjectLock getLock() {
    mutexInstanceRegister.lock();
    try {
      Long id = idSequence++;
      StorageObjectLock result = new StorageObjectLock(this, id);
      instanceRegister.put(id, new InstanceEntry(result));
      return result;
    } finally {
      mutexInstanceRegister.unlock();
    }
  }

  /**
   * This function is lately ensure that we own the physical lock for an object.
   */
  void ensurePhysicalLock() {
    if (acquirePhysicalLock == null) {
      return;
    }
    mutexInstanceRegister.lock();
    try {
      if (physicalLock == null) {
        physicalLock = acquirePhysicalLock.get();
      }
    } finally {
      mutexInstanceRegister.unlock();
    }
  }

  /**
   * The leave operation release the lock. If this thread is last one then this will execute the
   * cleanup and release the physical lock if any.
   */
  public void releaseLock(StorageObjectLock lock) {
    if (lock == null) {
      return;
    }
    mutexInstanceRegister.lock();
    try {
      // Remove ourself from the register and if we were the last one then release the in memory and
      // the physical lock also.
      instanceRegister.remove(lock.getId());
      if (instanceRegister.isEmpty()) {
        if (releaser != null && physicalLock != null) {
          releaser.accept(physicalLock);
        }
        lockRemover.accept(objectURI);
      }
    } finally {
      mutexInstanceRegister.unlock();
    }
  }

  final URI getObjectURI() {
    return objectURI;
  }

  final ReentrantLock getMutex() {
    return mutexExecution;
  }

  final boolean isEmpty() {
    return instanceRegister.isEmpty();
  }

  final void setLockRemover(Consumer<URI> lockRemover) {
    this.lockRemover = lockRemover;
  }

}
