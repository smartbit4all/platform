package org.smartbit4all.api.invocation.exception;

import java.util.function.Supplier;

/**
 * Exception type used to signal that an operation may be retried after a specified delay.
 * <p>
 * This exception is intended for use in invocation and integration layers where an operation cannot
 * complete successfully at the moment of execution, but a retry is expected to succeed after
 * waiting for a defined period (e.g., rate limiting, transient network issues, temporary
 * remote-service unavailability, or back-pressure conditions).
 * </p>
 *
 * <h2>Retry Semantics</h2> Throwing this exception indicates that:
 * <ul>
 * <li>The failure is non-terminal and the caller may retry the operation.</li>
 * <li>A recommended delay before retrying is provided via {@code retryAfterMillis}.</li>
 * <li>The caller is expected to handle or propagate retry logic appropriately.</li>
 * </ul>
 *
 * <p>
 * The {@link #getRetryAfterMillis()} method exposes the caller-supplied retry delay, enabling retry
 * policies such as fixed sleep, exponential backoff, or respecting remote-service throttling
 * instructions.
 * </p>
 *
 * <h2>Utility Retry Method</h2>
 * <p>
 * This class also provides a retry helper method:
 * {@link #callWithRetry(java.util.function.Supplier, int)}. It repeatedly invokes an action that
 * may throw {@code RetryException}, applying the following behavior:
 * </p>
 * <ul>
 * <li>Retries until the maximum attempt count is reached.</li>
 * <li>Waits for the duration specified by {@code retryAfterMillis} between attempts.</li>
 * <li>On the final failed attempt, unwraps and rethrows the cause if it is a
 * {@link RuntimeException}; otherwise rethrows the original {@code RetryException}.</li>
 * <li>If interrupted during the retry wait, aborts and propagates an {@link IllegalStateException}
 * with the interrupt state preserved.</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * 
 * <pre>{@code
 * String result = RetryException.callWithRetry(
 *     () -> api.call(), // may throw RetryException
 *     5 // maximum retries
 * );
 * }</pre>
 *
 * <h2>Thread Safety</h2> This exception class is immutable and therefore thread-safe.
 *
 * <h2>Common Scenarios</h2>
 * <ul>
 * <li>Cloud or AI model APIs that return rate-limit retry timing</li>
 * <li>Temporary database/network unavailability</li>
 * <li>Eventually-consistent distributed resources</li>
 * <li>Transient service failures in orchestration pipelines</li>
 * </ul>
 */
public class RetryException extends RuntimeException {

  private final long retryAfterMillis;

  /**
   * Creates a {@code RetryException} with the given retry delay.
   *
   * @param retryAfterMillis the recommended delay (in milliseconds) before retrying
   */
  public RetryException(long retryAfterMillis) {
    super();
    this.retryAfterMillis = retryAfterMillis;
  }

  /**
   * Creates a {@code RetryException} with a message, a cause, and a retry delay.
   *
   * @param message detail message describing the reason for the retry
   * @param cause underlying cause of the failure, if any
   * @param retryAfterMillis the recommended delay (in milliseconds) before retrying
   */
  public RetryException(String message, Throwable cause, long retryAfterMillis) {
    super(message, cause);
    this.retryAfterMillis = retryAfterMillis;
  }

  /**
   * Returns the number of milliseconds the caller should wait before retrying.
   *
   * @return recommended retry delay in milliseconds
   */
  public long getRetryAfterMillis() {
    return retryAfterMillis;
  }

  /**
   * Executes the supplied action and retries it when a {@link RetryException} is thrown, until the
   * configured maximum retry count is reached.
   * <p>
   * Behavior:
   * <ul>
   * <li>Executes the action immediately.</li>
   * <li>If a {@code RetryException} occurs, waits the duration specified by
   * {@link #getRetryAfterMillis()} before retrying.</li>
   * <li>Stops retrying once {@code maxRetries} attempts have been made.</li>
   * <li>If the final failure has a {@link RuntimeException} cause, rethrows that cause.</li>
   * <li>Otherwise rethrows the last {@code RetryException}.</li>
   * <li>If interrupted during sleep, throws an {@link IllegalStateException} and restores the
   * interrupted status.</li>
   * </ul>
   *
   * @param action the operation to execute, which may throw {@code RetryException}
   * @param maxRetries maximum number of retry attempts (must be ≥ 1)
   * @param <T> type returned by the action
   *
   * @return the result of the successful execution
   *
   * @throws RetryException if retries are exhausted without success
   * @throws RuntimeException if the final failure wraps a runtime cause
   * @throws IllegalStateException if interrupted while waiting between retries
   */
  public static <T> T callWithRetry(Supplier<T> action, int maxRetries) {
    int attempt = 0;

    while (true) {
      try {
        return action.get();
      } catch (RetryException e) {
        attempt++;

        if (attempt >= maxRetries) {
          Throwable cause = e.getCause();
          if (cause instanceof RuntimeException runtime) {
            throw runtime;
          }
          throw e;
        }

        long sleepMillis = e.getRetryAfterMillis();
        if (sleepMillis > 0) {
          try {
            Thread.sleep(sleepMillis);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", ie);
          }
        }
      }
    }
  }
}
