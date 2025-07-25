package org.smartbit4all.sql.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.object.DataSourceContextHolder;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.storage.fs.FSTestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
    classes = {
        StorageSQLConnectionPoolTestConfig.class
    },
    properties = {
        "platform.sql.temptable-autocreate.enabled=true"
    })
@ActiveProfiles(value = "connectionpool")
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class StorageSQLConnectionPoolTest {

  @Autowired
  protected ObjectApi objectApi;

  @Autowired(required = false)
  @Lazy
  protected PlatformTransactionManager transactionManager;


  @Test
  void testConnectionPooling() throws InterruptedException {
    ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

    URI uriToQuery = objectApi.saveAsNew("storage-connectionpool", new FSTestBean("SucceedTest"));

    // Thread 1: Hold a connection open for a while
    Thread thread1 = new Thread(() -> {
      try {
        DataSourceContextHolder.set("custom");
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        String title = transaction.execute(status -> queryObject(uriToQuery, 10000));
        assertEquals("SucceedTest", title);
      } catch (Throwable t1) {
        errors.add(t1);
      }
    });

    thread1.start();

    Thread.sleep(500); // ensure thread1 gets the only available connection

    // Thread 2: Try to get a second connection from the same pool
    Thread thread2 = new Thread(() -> {
      try {
        DataSourceContextHolder.set("custom");
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        Assertions.assertThrows(CannotCreateTransactionException.class, () -> {
          transaction.execute(status -> queryObject(uriToQuery, 0));
        });
      } catch (Throwable t1) {
        errors.add(t1);
      }
    });

    thread2.start();

    // Thread 3: Try to get a connection from a different pool
    Thread thread3 = new Thread(() -> {
      try {
        DataSourceContextHolder.set("custom");
        DataSourceContextHolder.setSystem();
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        String title = transaction.execute(status -> queryObject(uriToQuery, 0));
        assertEquals("SucceedTest", title);
      } catch (Throwable t1) {
        errors.add(t1);
      }
    });

    thread3.start();

    thread1.join();
    thread2.join();
    thread3.join();

    if (!errors.isEmpty()) {
      // Report the first error; you could also collect and report all
      Throwable first = errors.peek();
      throw new AssertionError("Assertion failed in one of the threads", first);
    }
  }


  private String queryObject(URI uriToQuery, long sleep) {
    FSTestBean object = objectApi.load(uriToQuery).getObject(FSTestBean.class);
    if (sleep != 0) {
      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {
      }
    }
    return object.getTitle();
  }
}
