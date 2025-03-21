/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.sql.service.modify;

import java.sql.Statement;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Utility class for SQL batch operations.
 */
public final class SQLBatchUtils {

  /**
   * Default batch size for database operations.
   */
  public static final int DEFAULT_BATCH_SIZE = 100;

  /**
   * Minimum batch size - if batch size is set to this or lower, batch operations will be disabled.
   */
  public static final int MIN_BATCH_SIZE = 1;

  /**
   * Private constructor to prevent instantiation.
   */
  private SQLBatchUtils() {
    // Utility class should not be instantiated
  }

  /**
   * Validates and adjusts the batch size to ensure it's at least the minimum value.
   * 
   * @param batchSize The requested batch size
   * @return The adjusted batch size (at least MIN_BATCH_SIZE)
   */
  public static int validateBatchSize(int batchSize) {
    return Math.max(MIN_BATCH_SIZE, batchSize);
  }

  /**
   * Executes a batch operation and calculates the total number of affected rows.
   * 
   * @param jdbcTemplate The JdbcTemplate to use for execution
   * @param sql The SQL statement to execute
   * @param batchSetter The BatchPreparedStatementSetter for setting parameters
   * @return The total number of affected rows
   */
  public static int executeBatch(JdbcTemplate jdbcTemplate, String sql,
      BatchPreparedStatementSetter batchSetter) {
    int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchSetter);
    // Calculate total affected rows
    int totalCount = 0;
    for (int count : updateCounts) {
      if (count == Statement.SUCCESS_NO_INFO) {
        totalCount++;
      } else if (count > 0) {
        totalCount += count;
      }
    }
    return totalCount;
  }

  /**
   * Checks if batch processing should be used based on the batch size and the amount of data.
   * 
   * @param batchSize The configured batch size
   * @return True if batch processing should be used, false otherwise
   */
  public static boolean useBatchProcessing(int batchSize) {
    return batchSize > MIN_BATCH_SIZE;
  }

}
