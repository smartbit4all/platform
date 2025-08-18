package org.smartbit4all.api.invocation.bean;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.ScheduledTestApi;

public class ScheduledTestApiImpl implements ScheduledTestApi {
  private static final Logger log = LoggerFactory.getLogger(ScheduledTestApiImpl.class);

  public static Integer counter = 0;

  public static LocalDateTime startTime = null;

  @Override
  public void incrementBy(Integer number) {
    if (startTime == null) {
      startTime = LocalDateTime.now();
    }
    log.info("Scheduled job execution {}", counter);
    counter++;
  }

}
