package org.smartbit4all.domain.data;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * TODO Under construction
 * 
 * The {@link DataLock} is responsible for getting mutual exclusive access to a given data. The data
 * is defined by the {@link TableData} and the rows.
 * 
 * @author Peter Boros
 */
public class DataLock implements Lock {

  @Override
  public void lock() {
    // TODO Auto-generated method stub

  }

  @Override
  public void lockInterruptibly() throws InterruptedException {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean tryLock() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void unlock() {
    // TODO Auto-generated method stub

  }

  @Override
  public Condition newCondition() {
    // TODO Auto-generated method stub
    return null;
  }

}
