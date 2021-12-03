package org.smartbit4all.storage.fs;

import java.util.UUID;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class StorageTransactionManager extends AbstractPlatformTransactionManager {

  @Override
  protected Object doGetTransaction() throws TransactionException {
    return UUID.randomUUID();
  }

  @Override
  protected void doBegin(Object transaction, TransactionDefinition definition)
      throws TransactionException {
    if (transaction instanceof UUID) {
      System.out.println("Begin transaction: " + transaction.toString());
    }
  }

  @Override
  protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
    if (status.getTransaction() instanceof UUID) {
      System.out.println("Commit: " + status.getTransaction().toString());
    }
  }

  @Override
  protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
    if (status.getTransaction() instanceof UUID) {
      System.out.println("Rollback: " + status.getTransaction().toString());
    }
  }

}
