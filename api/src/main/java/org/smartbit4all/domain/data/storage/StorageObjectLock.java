package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.smartbit4all.api.invocation.AsyncInvocationRequestEntry;

/**
 * The {@link StorageObjectLock} is a memory lock for the object URIs managed by an
 * {@link ObjectStorage}. To ensure the mutual exclusion during the write operation of an object
 * this {@link StorageObjectLock} will be created and managed till the end of the operation. The
 * specialty of this lock is that the given implementation can extend with a physical lock that is
 * acquired by the OS level process. In a simple situation when there is only one instance from an
 * application there is no need to apply the physical locking.
 *
 *
 * One time usage!!!
 *
 *
 * @author Peter Boros
 */
public final class StorageObjectLock implements Lock {

  /**
   * The lock entry held be the {@link ObjectStorage} implementation.
   */
  private StorageObjectLockEntry entry;

  /**
   * The unique identifier inside the {@link #entry}.
   */
  private final Long id;

  private final ObjectStorage objectStorage;

  StorageObjectLock(StorageObjectLockEntry entry, Long id, ObjectStorage objectStorage) {
    super();
    this.entry = entry;
    this.id = id;
    this.objectStorage = objectStorage;
  }

  public final URI getObjectURI() {
    check();
    return entry.getObjectURI();
  }

  @Override
  public void lock() {
    check();
    entry.getMutex().lock();
  }

  @Override
  public void lockInterruptibly() throws InterruptedException {
    check();
    entry.getMutex().lockInterruptibly();
  }

  @Override
  public boolean tryLock() {
    check();
    return entry.getMutex().tryLock();
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    check();
    return entry.getMutex().tryLock(time, unit);
  }

  @Override
  public void unlock() {
    check();
    objectStorage.unlock(this);
  }

  void unlockInternal() {
    entry.getMutex().unlock();
    release();
  }

  private final void check() {
    if (entry == null) {
      throw new IllegalStateException("The lock has been released already.");
    }
    entry.ensurePhysicalLock();
  }

  @Override
  public Condition newCondition() {
    throw new UnsupportedOperationException(
        "The storage object lock is not supporting conditions.");
  }

  /**
   * The unique identifier of the given lock instance.
   *
   * @return
   */
  final Long getId() {
    return id;
  }

  /**
   * Release the given object. We won't be able to use it again.
   */
  final void release() {
    if (entry != null) {
      entry.releaseLock(this);
      entry = null;
    }
  }

}
