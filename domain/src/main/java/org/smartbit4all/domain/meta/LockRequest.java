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
