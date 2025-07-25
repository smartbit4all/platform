package org.smartbit4all.api.object;

import java.util.function.Supplier;

/**
 * Utility for executing code blocks with a specific DataSource context.
 */
public class DataSourceContextTemplate {

  /**
   * Executes the given {@link Runnable} within the specified data source context.
   *
   * @param dataSourceKey the lookup key (e.g., "system", "lock")
   * @param task the task to run
   */
  public void executeWith(String dataSourceKey, Runnable task) {
    try {
      DataSourceContextHolder.set(dataSourceKey);
      task.run();
    } finally {
      DataSourceContextHolder.clear();
    }
  }

  /**
   * Executes the given {@link Supplier} within the specified data source context, returning a
   * result.
   *
   * @param dataSourceKey the lookup key (e.g., "system", "lock")
   * @param task the task to run
   * @param <T> the return type
   * @return the result of the task
   */
  public <T> T executeWith(String dataSourceKey, Supplier<T> task) {
    try {
      DataSourceContextHolder.set(dataSourceKey);
      return task.get();
    } finally {
      DataSourceContextHolder.clear();
    }
  }
}
