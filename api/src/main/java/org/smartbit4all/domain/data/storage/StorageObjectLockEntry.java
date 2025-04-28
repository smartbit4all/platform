package org.smartbit4all.domain.data.storage;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.utility.ListBasedMap;

/**
 * This entry is the common lock entry for the {@link ObjectStorage} implementations.
 *
 * @author Peter Boros
 */
final class StorageObjectLockEntry {

  private static final Logger log = LoggerFactory.getLogger(StorageObjectLockEntry.class);

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
  private volatile long idSequence = 0;

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
   * first {@link StorageObjectLock} is activated via an operation. Boolean para
   */
  private Function<Boolean, StorageObjectPhysicalLock> acquirePhysicalLock;

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

  private Consumer<StorageObjectLock> unlocker;

  private Function<StorageObjectLock, StorageObjectLockEntry> lockReattacher;

  /**
   * Implies that the given lock entry is removing currently. So the threads trying to get lock has
   * to restart the get lock function.
   */
  private boolean removing = false;

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
      Function<Boolean, StorageObjectPhysicalLock> acquire,
      Consumer<StorageObjectPhysicalLock> releaser,
      Consumer<StorageObjectLock> unlocker,
      Function<StorageObjectLock, StorageObjectLockEntry> lockReattacher) {
    super();
    this.objectURI = objectURI;
    if (acquire != null) {
      if (releaser == null) {
        throw new IllegalArgumentException(
            "Unable to initate the StorageObjectLock, the the physical lock release method is missing.");
      }
      this.releaser = releaser;
      this.acquirePhysicalLock = acquire;
    }
    this.unlocker = unlocker;
    this.lockReattacher = lockReattacher;
  }

  /**
   * Register a new instance to the entry. This doesn't mean lock because the lock can be placed
   * with {@link StorageObjectLock#lock()}.
   *
   * @return The new lock instance.
   * @throws InterruptedException, InterruptedException
   */
  StorageObjectLock getLock() throws StorageObjectLockEntryRemovingException, InterruptedException {
    checkIfRemoving();
    while (!mutexInstanceRegister.tryLock(10, TimeUnit.MILLISECONDS)) {
      checkIfRemoving();
    }
    try {
      checkIfRemoving();
      Long id = idSequence++;
      StorageObjectLock result =
          new StorageObjectLock(this, id, objectURI, unlocker, lockReattacher);
      instanceRegister.put(id, new InstanceEntry(result));
      return result;
    } finally {
      mutexInstanceRegister.unlock();
    }
  }

  private void checkIfRemoving() {
    if (removing) {
      throw new StorageObjectLockEntryRemovingException();
    }
  }

  /**
   * This function is lately ensure that we own the physical lock for an object.
   *
   * @return returns if physical lock acquired
   */
  boolean ensurePhysicalLock(boolean nowait) {
    if (acquirePhysicalLock == null) {
      // no acquire callback, assume physical lock is always present
      return true;
    }
    checkIfRemoving();
    mutexInstanceRegister.lock();
    try {
      checkIfRemoving();
      if (physicalLock == null) {
        physicalLock = acquirePhysicalLock.apply(nowait);
      }
      return physicalLock != null;
    } finally {
      mutexInstanceRegister.unlock();
    }
  }

  /**
   * The leave operation release the lock. If this thread is last one then this will execute the
   * cleanup and release the physical lock if any.
   */
  void releaseLock(StorageObjectLock lock) {
    if (lock == null) {
      return;
    }
    mutexInstanceRegister.lock();
    try {
      if (removing) {
        // this should never happen, it's here to guard against multiple physical lock removal
        return;
      }
      // Remove ourself from the register and if we were the last one then release the in memory and
      // the physical lock also.
      instanceRegister.remove(lock.getId());
      if (instanceRegister.isEmpty()) {
        removing = true;
      }
    } finally {
      mutexInstanceRegister.unlock();
    }
    if (removing) {
      if (releaser != null && physicalLock != null) {
        releaser.accept(physicalLock);
      }
      lockRemover.accept(objectURI);
    }
  }

  void reattachLock(StorageObjectLock lock) {
    checkIfRemoving();
    mutexInstanceRegister.lock();
    try {
      checkIfRemoving();
      Long id = lock.getId();
      // check if lock.id is below this entry's idSequence. if not, generate new id
      if (id >= idSequence) {
        id = idSequence++;
        lock.setId(id);
      }
      if (!instanceRegister.containsKey(id)) {
        // removed already, put it back
        instanceRegister.put(id, new InstanceEntry(lock));
      } else {
        // not removed yet, may be conflict
        InstanceEntry instanceEntry = instanceRegister.get(id);
        StorageObjectLock existingLock = instanceEntry.instance.get();
        if (existingLock == null) {
          // weakRef gone, replace existing instance
          instanceEntry.instance = new WeakReference<>(lock);
        } else {
          // existing lock with id, probably it's a new StorageObjectLockEntry, with different ids
          id = idSequence++;
          lock.setId(id);
          instanceRegister.put(id, new InstanceEntry(lock));
        }
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
