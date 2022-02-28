package org.smartbit4all.core.io.utility;

/**
 * The file lock is about the runtime instance id and the transaction id. This is used for locking
 * files and helps defining if the given lock is stiull valid.
 * 
 * @author Peter Boros
 */
public class FileLockData {

  /**
   * The id (UUID) of the runtime that owns the lock.
   */
  private String runtimeId;

  /**
   * The transaction identifier that is responsible for the given lock.
   */
  private String transactionId;

  public FileLockData(String runtimeId, String transactionId) {
    super();
    this.runtimeId = runtimeId;
    this.transactionId = transactionId;
  }

  public final String getRuntimeId() {
    return runtimeId;
  }

  public final void setRuntimeId(String runtimeId) {
    this.runtimeId = runtimeId;
  }

  public final String getTransactionId() {
    return transactionId;
  }

  public final void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

}
