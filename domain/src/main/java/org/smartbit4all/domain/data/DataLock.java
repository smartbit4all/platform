/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
