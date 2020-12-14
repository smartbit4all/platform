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
package org.smartbit4all.domain.meta;

import org.smartbit4all.domain.service.query.Query;

/**
 * The lock request defines the required lock for the {@link Query} and other functions.
 * 
 * @author Peter Boros
 */
public class LockRequest {

  /**
   * The lock timeout for the lock request.
   * <ul>
   * <li>-1 : Wait forever without timeout.</li>
   * <li>0 : No wait - if any of the rows are locked then throw an exception immediately.</li>
   * <li>>0 : Wait until the timeout. The timeout is in millisecond.</li>
   * </ul>
   */
  private long timeOut = -1;

  public LockRequest(long timeOut) {
    super();
    this.timeOut = timeOut;
  }

  public long timeOut() {
    return timeOut;
  }

  public void setTimeOut(long timeOut) {
    this.timeOut = timeOut;
  }

}
