package org.smartbit4all.sql.storage;

import javax.sql.DataSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class StorageSqlAwareJdbcTransactionManager extends JdbcTransactionManager {

  private final StorageSQL storageSQL;

  public StorageSqlAwareJdbcTransactionManager(
      final DataSource dataSource,
      final StorageSQL storageSQL) {
    super(dataSource);
    this.storageSQL = storageSQL;
  }

  @Override
  protected void prepareForCommit(DefaultTransactionStatus status) {
    super.prepareForCommit(status);
    if (status.getSuspendedResources() == null) {
      storageSQL.ensureTransactionHandler();
    }
  }

}
