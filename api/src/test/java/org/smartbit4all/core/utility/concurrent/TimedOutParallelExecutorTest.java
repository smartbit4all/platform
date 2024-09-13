package org.smartbit4all.core.utility.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedOutParallelExecutorTest {

  private static final Logger log = LoggerFactory.getLogger(TimedOutParallelExecutorTest.class);

  private static final long timeout = 2000 * 2;

  @Test
  @Disabled // test is disabled because on millisecond scale it is not consistent
  void test() throws InterruptedException, ExecutionException {

    List<Integer> numbers = IntStream.range(1, 10).boxed().collect(Collectors.toList());

    TimedOutParallelExecutor.doTaskParallel(numbers, this::longRunningTask, 2, timeout,
        "parallel-test");


  }

  private void waitSomeTime(Integer base) {
    Integer sleepTime = base * 2000;
    try {
      log.debug("sleeping on number: {}", base);
      Thread.sleep(sleepTime.longValue());
      if (sleepTime > timeout) {
        // should be interrupted by now
        fail();
      }
      log.debug("awekening on number: {}", base);
    } catch (InterruptedException e) {
      log.debug("Task interrupted due to timeout for number: {}", base);
      if (sleepTime < timeout) {
        // should not be interrupted at this time
        fail();
      }
    }
  }

  private void longRunningTask(Integer base) {
    long sleepTime = base * 2000;
    long startTime = System.currentTimeMillis();
    try {
      log.debug("Starting task for number: {}", base);

      long endTime = startTime + sleepTime; // Simulate base * 2000ms
      while (System.currentTimeMillis() < endTime) {
        // Simulating some work (e.g., complex computation)
        for (int i = 0; i < Integer.MAX_VALUE / 100; i++) {
          Math.sqrt(i); // Arbitrary computation to simulate CPU load
        }

        // Check for interruption periodically
        if (Thread.currentThread().isInterrupted()) {
          throw new InterruptedException("Task was interrupted");
        }
      }
      long runFor = System.currentTimeMillis() - startTime;
      // log.debug("sleep: {}, timeout: {}, runFor: {}", sleepTime, timeout,
      // runFor);
      //
      // assertThat(runFor).isLessThanOrEqualTo(timeout);

      log.debug("Task completed for number: {}, sleep: {}, timeout: {}, runFor: {}", base,
          sleepTime, timeout, runFor);
    } catch (InterruptedException e) {
      long runFor = System.currentTimeMillis() - startTime;
      log.debug(
          "Task interrupted due to timeout for number: {}, sleep: {}, timeout: {}, runFor: {}",
          base, sleepTime, timeout, runFor);
      // should not be interrupted at this time
      assertThat(runFor).isGreaterThanOrEqualTo(timeout);
    }
  }

}
