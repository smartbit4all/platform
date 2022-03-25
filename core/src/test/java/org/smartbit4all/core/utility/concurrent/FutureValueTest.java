package org.smartbit4all.core.utility.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FutureValueTest {

  private static final Logger log = LoggerFactory.getLogger(FutureValueTest.class);

  Executor executor = Executors.newFixedThreadPool(5);

  List<String> sequence = new ArrayList<>();

  FutureValue<String> futureValue1 = new FutureValue<>();

  FutureValue<String> futureValue2 = new FutureValue<>();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void test() throws InterruptedException, ExecutionException {
    // 5 more thread waiting for execution.
    for (int i = 0; i < 10; i++) {
      executor.execute(() -> {
        try {
          String string = futureValue1.get();
          sequence.add(string);
          if (sequence.size() == 10) {
            futureValue2.setValue("2");
          }
        } catch (Exception e) {
          log.error("Error", e);
        }
      });
    }

    Thread.sleep(200);

    futureValue1.setValue("1");

    sequence.add(futureValue2.get());

    Assertions.assertEquals("[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2]", sequence.toString());

  }

}
