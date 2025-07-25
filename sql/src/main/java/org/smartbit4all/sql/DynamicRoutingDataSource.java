package org.smartbit4all.sql;

import org.smartbit4all.api.object.DataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * A dynamic routing DataSource that delegates to different target DataSources based on a context
 * key held in {@link DataSourceContextHolder}.
 *
 * <p>
 * This allows using multiple connection pools with different configurations (e.g., pool size,
 * timeout) even if they share the same credentials or database.
 * </p>
 *
 * <p>
 * This class extends {@link AbstractRoutingDataSource} and overrides
 * {@code determineCurrentLookupKey()} to return the context-based key that decides which DataSource
 * to use.
 * </p>
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

  /**
   * Determine the current lookup key for routing to the target DataSource.
   *
   * @return the current lookup key (e.g., "SYSTEM", "LOCK", "REPORTING")
   */
  @Override
  protected Object determineCurrentLookupKey() {
    return DataSourceContextHolder.get();
  }
}
