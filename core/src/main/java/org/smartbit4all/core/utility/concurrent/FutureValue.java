package org.smartbit4all.core.utility.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple trick to have the same {@link Future} interface for awaiting a value even if we are not
 * the initiator of the execution. In this case there is an other thread that producing a given
 * value. We don't have any idea about the execution time and we don't have reference for the given
 * thread. Ergo we cann't synchronize. So we need a waiting mechanism for the value it will set
 * later on.
 * 
 * @author Peter Boros
 *
 * @param <V>
 */
public class FutureValue<V> implements Future<V> {

  /**
   * The value we are waiting for.
   */
  V value = null;

  Exception e = null;

  /**
   * The count down for the one execution.
   */
  private final CountDownLatch cnt = new CountDownLatch(1);

  private boolean done = false;

  public void setValue(V value) {
    this.value = value;
    done = true;
    cnt.countDown();
  }

  public void setException(Exception e) {
    this.e = e;
    done = true;
    cnt.countDown();
  }

  public Exception getException() {
    return e;
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    cnt.countDown();
    return true;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public boolean isDone() {
    return done;
  }

  @Override
  public V get() throws InterruptedException, ExecutionException {
    if (done) {
      return returnValue();
    }
    cnt.await();
    return returnValue();
  }

  private final V returnValue() throws ExecutionException {
    if (e != null) {
      throw new ExecutionException(e);
    }
    return value;
  }

  @Override
  public V get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return get();
  }

}
