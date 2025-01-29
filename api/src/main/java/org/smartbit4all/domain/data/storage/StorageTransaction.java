package org.smartbit4all.domain.data.storage;

import org.smartbit4all.api.storage.bean.TransactionData;

/**
 * The storage transaction is a generic transaction object for the transactions managed by the
 * {@link ObjectStorage} implementations. It contains the {@link TransactionData} object that is the
 * stored domain object about a transaction.
 *
 * @author Peter Boros
 */
public class StorageTransaction {

  private final TransactionData data;

  public StorageTransaction(TransactionData data) {
    super();
    this.data = data;
  }

  public final TransactionData getData() {
    return data;
  }

}
