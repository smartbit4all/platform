package org.smartbit4all.domain.data.storage;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The {@link StorageObjectLock} is a memory lock for the {@link StorageObject}s managed by an
 * {@link ObjectStorage}. To ensure the mutual exclusion during the write operation of an object
 * this {@link StorageObjectLock} will be created and managed till the end of the operation. The
 * specialty of this lock is that the given implementation can extend with a physical lock that is
 * acquired by the OS level process. In a simple situation when there is only one instance from an
 * application there is no need to apply the physical locking.
 * 
 * @author Peter Boros
 */
public class StorageObjectLock {

  /**
   * The URI of the object the lock belongs to.
   */
  private URI objectURI;

  /**
   * The lock that ensure the mutual exclusion.
   */
  private Lock mutexCounter = new ReentrantLock(true);

  /**
   * The mutex to ensure the exclusive execution.
   */
  private Lock mutexExecution = new ReentrantLock(true);

  /**
   * The counter of the threads dealing with the given object. By default it's 1 because the first
   * thread creates the object lock itself. When a thread tries to {@link #leave()} and it was the
   * last one the counter will be zero and the destruction begins. The destruction process will
   * remove the physical lock if any and all other threads must wait for the finish of the operation
   * create a new lock instance. But if there are other threads dealing with the same object then
   * the counter will be larger than zero when leaving so the lock remains and another thread will
   * do the destruction job. In this case the
   */
  private int threadCount = 1;

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
  private final Consumer<URI> lockRemover;

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
   * @throws IOException
   */
  StorageObjectLock(URI objectURI, Consumer<URI> lockRemover,
      Supplier<StorageObjectPhysicalLock> acquire,
      Consumer<StorageObjectPhysicalLock> releaser) {
    super();
    this.lockRemover = lockRemover;
    if (acquire != null) {
      if (releaser == null) {
        throw new IllegalArgumentException(
            "Unable to initate the StorageObjectLock, the the physical lock release method is missing.");
      }
      this.releaser = releaser;
      physicalLock = acquire.get();
    }
  }

  /**
   * This function will register the actual thread if the object lock is still active. The active
   * means that count is larger than zero and the destruction hasn't been started. When the last
   * thread detects
   * 
   * @return If return true then we have the lock and we can start working. Otherwise the lock has
   *         been destroyed in the mean time so we have to initiate a new lock!
   */
  public boolean enter() {
    boolean result = false;
    // When entering we must wait for the execution even if we will be able to run or we just need
    // to recreate the object lock. In both cases the current process must be finished before
    // continuing.
    mutexExecution.lock();
    mutexCounter.lock();
    try {
      if (threadCount > 0) {
        threadCount++;
        result = true;
      }
    } finally {
      mutexCounter.unlock();
    }
    return result;
  }

  /**
   * The leave operation release the lock. If this thread is last one then this will execute the
   * cleanup and release the physical lock if any.
   */
  public void leave() {
    mutexCounter.lock();
    try {
      threadCount--;
      if (threadCount == 0) {
        if (releaser != null && physicalLock != null) {
          releaser.accept(physicalLock);
        }
        lockRemover.accept(objectURI);
      }
    } finally {
      mutexCounter.unlock();
      mutexExecution.unlock();
    }
  }



  public final URI getObjectURI() {
    return objectURI;
  }

}
