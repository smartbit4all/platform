package org.smartbit4all.storage.fs;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.AsyncInvocationRequestEntry;
import org.smartbit4all.api.storage.bean.TransactionData;
import org.smartbit4all.api.storage.bean.TransactionState;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageSaveEvent;
import org.smartbit4all.domain.data.storage.StorageTransaction;
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

  private StorageFS storageFS;

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
  ThreadLocal<StorageTransaction> storageTransactionObject = new ThreadLocal<>();

  /**
   * The transaction attached to the current {@link Thread}.
   */
  ThreadLocal<List<AsyncInvocationRequestEntry>> invocations = new ThreadLocal<>();

  public StorageTransactionManagerFS(StorageFS storageFS) {
    super();
    this.storageFS = storageFS;
  }

  /**
   * @return true of the current thread has an active transaction.
   */
  public boolean isInTransaction() {
    return storageTransactionObject.get() != null;
  }

  /**
   * @return The value object of the current transaction if any. If there is no active transaction
   *         on this thread then we get back null.
   */
  public StorageTransaction getCurrentTransaction() {
    return storageTransactionObject.get();
  }

  /**
   * Adds an on succeed event to the actual transaction. Or creates a new transaction if it's not
   * exists.
   * 
   * @param object
   * @param event
   */
  public void addOnSucceed(StorageObject<?> object, StorageSaveEvent event) {

    StorageTransaction storageTransaction = storageTransactionObject.get();
    if (storageTransaction == null) {
      storageTransaction = new StorageTransaction(null);
      storageTransactionObject.set(storageTransaction);
    }
    storageTransaction.addSaveEventItem(object, event);
  }

  /**
   * Adds an on succeed invocation to the actual transaction.
   * 
   * @param request
   */
  public void addOnSucceed(AsyncInvocationRequestEntry request) {
    List<AsyncInvocationRequestEntry> invocationList = invocations.get();
    if (invocationList == null) {
      invocationList = new ArrayList<>();
      invocations.set(invocationList);
    }
    invocationList.add(request);
  }

  @Override
  protected Object doGetTransaction() throws TransactionException {
    OffsetDateTime now = OffsetDateTime.now();
    TransactionData data =
        new TransactionData().startTime(now).lastTouch(now).state(TransactionState.EXEC);
    StorageTransaction storageTransaction = new StorageTransaction(data);
    return storageTransaction;
  }

  @Override
  protected void doBegin(Object transaction, TransactionDefinition definition)
      throws TransactionException {
    if (transaction instanceof StorageTransaction) {
      StorageTransaction storageTransaction = (StorageTransaction) transaction;
      // TODO privileged save for off transaction save.
      storage.get().saveAsNew(storageTransaction.getData());
      storageTransactionObject.set(storageTransaction);
      log.debug("Begin the {1} transaction", storageTransaction);
    }
  }

  @Override
  protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
    if (status.getTransaction() instanceof StorageTransaction) {
      finishTransaction((StorageTransaction) status.getTransaction(), TransactionState.SUCC);
      // Call invocations
      List<AsyncInvocationRequestEntry> list = invocations.get();
      if (list != null) {
        for (AsyncInvocationRequestEntry asyncInvocation : list) {
          asyncInvocation.invoke();
        }
      }
      invocations.remove();
      // Call the events.
      Map<StorageObject<?>, List<StorageSaveEvent>> events =
          ((StorageTransaction) status.getTransaction()).getSaveEvents();
      if (events != null) {
        for (Entry<StorageObject<?>, List<StorageSaveEvent>> entry : events.entrySet()) {
          if (entry.getValue() != null) {
            for (StorageSaveEvent event : entry.getValue()) {
              if (event != null) {
                storageFS.invokeOnSucceedFunctions(entry.getKey(), event);
              }
            }
          }
        }
      }
    }
  }

  @Override
  protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
    if (status.getTransaction() instanceof StorageTransaction) {
      finishTransaction((StorageTransaction) status.getTransaction(), TransactionState.FAIL);
    }
  }

  private final void finishTransaction(StorageTransaction storageTransaction,
      TransactionState state) {
    try {
      if (storageTransaction.getData().getState() == TransactionState.EXEC) {
        // If we are the currently executing transaction then we can commit this.
        OffsetDateTime now = OffsetDateTime.now();
        // TODO privileged save for off transaction save.
        storage.get().update(storageTransaction.getData().getUri(), TransactionData.class,
            t -> t.finishTime(now).state(state));
      }
    } finally {
      storageTransactionObject.remove();
    }
  }

}
