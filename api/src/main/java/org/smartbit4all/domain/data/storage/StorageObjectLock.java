package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.smartbit4all.core.object.ObjectApi;

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
    if (check(true)) {
      return entry.getMutex().tryLock();
    }
    // physical lock not acquired, don't wait for it
    return false;
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

  /**
   * Use this very carefully! It will unlock this lock and if it was the last lock for this
   * objectUri, it will also release the phyiscal lock! One known usage is
   * {@link ObjectApi#lockAll(java.util.List)}, where if a tryLock succeeds, but later tryLock
   * fails, previous locks should be unlock right now, don't wait till the end of transaction.
   */
  public void unlockIgnoreTransaction() {
    check();
    unlockInternal();
  }

  private final void check() {
    check(false);
  }

  private final boolean check(boolean nowait) {
    if (entry == null) {
      throw new IllegalStateException("The lock has been released already.");
    }
    return entry.ensurePhysicalLock(nowait);
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
  public final void release() {
    if (entry != null) {
      entry.releaseLock(this);
      entry = null;
    }
  }

}
