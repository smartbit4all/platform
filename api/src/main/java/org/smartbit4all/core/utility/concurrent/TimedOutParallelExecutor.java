package org.smartbit4all.core.utility.concurrent;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedOutParallelExecutor {

  private static final Logger log = LoggerFactory.getLogger(TimedOutParallelExecutor.class);

  private TimedOutParallelExecutor() {}

  public static <T> void doInParallelStream(Collection<T> input, Consumer<T> task, int threadCount,
      long timeoutInMillis, String threadName) {

    ExecutorService executor =
        Executors.newFixedThreadPool(threadCount, new CustomThreadFactory(threadName));

    try {
      input
          .forEach(inputItem -> processWithTimeoutVoid(inputItem, task, executor, timeoutInMillis));

    } finally {
      // Shut down the executor service
      executor.shutdown();
    }
  }

  public static <T, R> Stream<R> doInParallelStream(Collection<T> input, Function<T, R> task,
      int threadCount,
      long timeoutInMillis, String threadName) {

    ExecutorService executor =
        Executors.newFixedThreadPool(threadCount, new CustomThreadFactory(threadName));

    try {
      return input.stream()
          .map(inputItem -> processWithTimeout(inputItem, task, executor, timeoutInMillis));

    } finally {
      // Shut down the executor service
      executor.shutdown();
    }
  }


  private static <T> void processWithTimeoutVoid(T inputItem, Consumer<T> task,
      ExecutorService executor, long timeoutInMillis) {
    CompletableFuture<Void> futureTask =
        CompletableFuture.runAsync(() -> task.accept(inputItem), executor);

    try {
      // Set a timeout for the task
      futureTask.get(timeoutInMillis, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      log.debug("Task has timed out on item [{}]", inputItem);
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException("Task execution failed", e);
    }
  }

  private static <T, R> R processWithTimeout(T inputItem, Function<T, R> task,
      ExecutorService executor, long timeoutInMillis) {
    CompletableFuture<R> futureTask =
        CompletableFuture.supplyAsync(() -> task.apply(inputItem), executor);

    try {
      // Set a timeout for the task
      return futureTask.get(timeoutInMillis, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      log.debug("Task has timed out on item [{}]", inputItem);
      return null;
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException("Task execution failed", e);
    }
  }

  private static class CustomThreadFactory implements ThreadFactory {
    private final String baseName;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public CustomThreadFactory(String baseName) {
      this.baseName = baseName;
    }

    @Override
    public Thread newThread(Runnable r) {
      // Create a new thread with a custom name pattern (e.g., CustomThread-1, CustomThread-2, ...)
      Thread thread = new Thread(r, baseName + "-" + threadNumber.getAndIncrement());
      return thread;
    }
  }
}
