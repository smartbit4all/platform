package org.smartbit4all.api.object;

import java.util.Stack;

/**
 * A thread-local context holder for managing a lookup key used in routing logic.
 *
 * <p>
 * This class allows setting, getting, and clearing a context-specific key that can be used to
 * determine routing behavior elsewhere in the application, such as selecting a particular database
 * connection pool.
 * </p>
 *
 * <p>
 * Each thread can independently manage its own key, making this class safe for concurrent use in
 * multi-threaded applications.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>
 * DataSourceContextHolder.set("system");
 * // perform operations
 * DataSourceContextHolder.clear();
 * </pre>
 */
public class DataSourceContextHolder {

  public static final String DATASOURCE_DEFAULT = "default";
  public static final String DATASOURCE_SYSTEM = "system";
  public static final String DATASOURCE_LOCK = "lock";

  /**
   * ThreadLocal to hold the current DataSource keys.
   */
  private static final ThreadLocal<Stack<String>> contextHolder =
      ThreadLocal.withInitial(Stack::new);

  /**
   * Set the current DataSource lookup key.
   *
   * @param dataSourceKey the lookup key corresponding to the DataSource
   */
  public static void set(String dataSourceKey) {
    contextHolder.get().push(dataSourceKey);
  }

  /**
   * Set the current DataSource lookup key to system.
   *
   */
  public static void setSystem() {
    set(DATASOURCE_SYSTEM);
  }

  /**
   * Set the current DataSource lookup key to lock.
   *
   */
  public static void setLock() {
    set(DATASOURCE_LOCK);
  }

  /**
   * Set the current DataSource lookup key to default.
   *
   */
  public static void setDefault() {
    set(DATASOURCE_DEFAULT);
  }

  /**
   * Get the current DataSource lookup key.
   *
   * @return the lookup key for the current thread
   */
  public static String get() {
    Stack<String> stack = contextHolder.get();
    if (!stack.isEmpty()) {
      return stack.peek();
    }
    return null;
  }

  /**
   * Clear the current DataSource lookup key.
   */
  public static void clear() {
    Stack<String> stack = contextHolder.get();
    if (!stack.isEmpty()) {
      stack.pop();
    }
  }
}
