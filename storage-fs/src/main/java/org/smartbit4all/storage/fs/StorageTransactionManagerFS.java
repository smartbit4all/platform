package org.smartbit4all.storage.fs;

import java.time.OffsetDateTime;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.storage.bean.TransactionData;
import org.smartbit4all.api.storage.bean.TransactionState;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * The {@link PlatformTransactionManager} implementation for the {@link StorageFS}. It can manage
 * transactions over the file system based schemaless object storage. The atomic operation is the
 * {@link StorageFS#save(org.smartbit4all.domain.data.storage.StorageObject)} where the success of
 * the operation is atomic. The transaction management is based on a lazy mode.
 * 
 * The operation ...
 * 
 * @author Peter Boros
 */
public class StorageTransactionManagerFS extends AbstractPlatformTransactionManager {

  private static final Logger log = LoggerFactory.getLogger(StorageTransactionManagerFS.class);

  @Autowired
  private StorageApi storageApi;

  /**
   * The logical scheme for the transactions.
   */
  public static final String TRANSACTION_SCHEME = "transaction";

  /**
   * This is the OrgApi scheme where we save the settings for the notify.
   */
  private Supplier<Storage> storage = new Supplier<Storage>() {

    private Storage storageInstance;

    @Override
    public Storage get() {
      if (storageInstance == null) {
        storageInstance = storageApi.get(TRANSACTION_SCHEME);
      }
      return storageInstance;
    }
  };

  /**
   * The transaction attached to the current {@link Thread}.
   */
  ThreadLocal<TransactionData> transactionData = new ThreadLocal<>();

  @Override
  protected Object doGetTransaction() throws TransactionException {
    OffsetDateTime now = OffsetDateTime.now();
    TransactionData data =
        new TransactionData().startTime(now).lastTouch(now).state(TransactionState.EXEC);
    return data;
  }

  @Override
  protected void doBegin(Object transaction, TransactionDefinition definition)
      throws TransactionException {
    if (transaction instanceof TransactionData) {
      TransactionData transactionData = (TransactionData) transaction;
      storage.get().saveAsNew(transactionData);
      log.debug("Begin the {1} transaction", transactionData);
    }
  }

  @Override
  protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
    if (status.getTransaction() instanceof TransactionData) {
      TransactionData transactionData = (TransactionData) status.getTransaction();
      StorageObject<TransactionData> so = storage.get().load(transactionData.getUri(),
          TransactionData.class);
      if (so.getObject().getState() == TransactionState.EXEC) {
        // If we are the currently executing transaction then we can commit this.
        OffsetDateTime now = OffsetDateTime.now();
        so.getObject().finishTime(now).state(TransactionState.SUCC);
        storage.get().save(so);
      }
    }
  }

  @Override
  protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
    if (status.getTransaction() instanceof TransactionData) {
      TransactionData transactionData = (TransactionData) status.getTransaction();
      StorageObject<TransactionData> so = storage.get().load(transactionData.getUri(),
          TransactionData.class);
      if (so.getObject().getState() == TransactionState.EXEC) {
        // If we are the currently executing transaction then we can set it failed.
        OffsetDateTime now = OffsetDateTime.now();
        so.getObject().finishTime(now).state(TransactionState.FAIL);
        storage.get().save(so);
      }
    }
  }

}
